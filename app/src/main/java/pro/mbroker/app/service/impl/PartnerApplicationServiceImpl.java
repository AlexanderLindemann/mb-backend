package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.enums.ApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.*;
import pro.mbroker.app.exception.AccessDeniedException;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.repository.PartnerApplicationRepository;
import pro.mbroker.app.repository.specification.BankApplicationSpecification;
import pro.mbroker.app.service.CalculatorService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.service.RealEstateService;
import pro.mbroker.app.util.Pagination;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.common.security.Permission;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationServiceImpl implements PartnerApplicationService {

    private static final String CREATED_AT = "createdAt";

    private final CalculatorService calculatorService;
    private final CurrentUserService currentUserService;
    private final PartnerService partnerService;
    private final CreditProgramRepository creditProgramRepository;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final BankApplicationRepository bankApplicationRepository;
    private final RealEstateService realEstateService;
    private final PartnerApplicationMapper partnerApplicationMapper;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final BankApplicationMapper bankApplicationMapper;


    @Override
    @Transactional(readOnly = true)
    public List<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        log.info("Getting all partner applications with pagination: page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);

        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        UUID partnerId = partnerService.getCurrentPartner().getId();
        return partnerApplicationRepository.findAllIsActive(partnerId, pageable);
    }

    @Override
    @Transactional
    public PartnerApplication createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = getPartnerApplication(request);
        BorrowerProfileRequest mainBorrower = request.getMainBorrower();
        BorrowerProfile borrowerProfile = borrowerProfileMapper.toBorrowerProfile(mainBorrower);
        borrowerProfile.setPartnerApplication(partnerApplication);
        List<BankApplication> bankApplications = buildBorrowerApplications(request, partnerApplication);
        bankApplications.forEach(app -> app.setMainBorrower(borrowerProfile));
        partnerApplication.setBankApplications(bankApplications);
        partnerApplication.getBorrowerProfiles().add(borrowerProfile);
        return partnerApplicationRepository.save(partnerApplication);
    }


    @Override
    @Transactional
    public PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication existingPartnerApplication = getPartnerApplication(partnerApplicationId);
        RealEstate realEstate = realEstateService.findById(request.getRealEstateId());
        Partner partner = realEstate.getPartner();
        partnerApplicationMapper.updatePartnerApplicationFromRequest(request, existingPartnerApplication);
        existingPartnerApplication.setPartner(partner);
        existingPartnerApplication.setRealEstate(realEstate);
        List<BankApplication> updatedBorrowerApplications = buildBorrowerApplications(request, existingPartnerApplication);
        existingPartnerApplication.setBankApplications(updatedBorrowerApplications);
        return partnerApplicationRepository.save(existingPartnerApplication);
    }

    @Override
    @Transactional
    public void deletePartnerApplication(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = getPartnerApplicationById(partnerApplicationId);
        partnerApplication.setActive(false);
        partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication) {
        PartnerApplicationResponse response = partnerApplicationMapper.toPartnerApplicationResponse(partnerApplication);
        List<BankApplication> borrowerApplications = partnerApplication.getBankApplications();
        Map<UUID, BorrowerProfile> borrowerProfileMap = partnerApplication.getBorrowerProfiles().stream()
                .collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
        IntStream.range(0, borrowerApplications.size())
                .forEach(i -> {
                    BankApplication borrowerApplication = borrowerApplications.get(i);
                    BankApplicationResponse bankApplicationResponse = response.getBankApplications().get(i);
                    BigDecimal mortgageSum = calculatorService.getMortgageSum(borrowerApplication.getRealEstatePrice(), borrowerApplication.getDownPayment());
                    bankApplicationResponse.setMortgageSum(mortgageSum);
                    bankApplicationResponse.setCoBorrowers(borrowerProfileMap.values().stream()
                            .filter(borrowerProfile -> !borrowerProfile.getId().equals(borrowerApplication.getMainBorrower().getId()))
                            .map(borrowerProfileMapper::toBorrowerProfileResponse)
                            .collect(Collectors.toList()));
                });
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerApplication getPartnerApplicationByIdWithPermission(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_ADMIN_ACCESS))) {
            checkPermission(authorities, partnerApplication);
        }
        return partnerApplication;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerApplication getPartnerApplication(UUID partnerApplicationId) {
        return partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, partnerApplicationId));
    }

    @Override
    public List<BankApplicationResponse> search(String firstName,
                                                String middleName,
                                                String lastName,
                                                String phoneNumber,
                                                String residentialComplexName,
                                                RegionType region,
                                                String bankName,
                                                ApplicationStatus applicationStatus,
                                                String sortBy,
                                                String sortDirection) {

        List<BankApplication> bankApplications = bankApplicationRepository
                .findAll(BankApplicationSpecification
                        .combineSearch(firstName,
                                middleName,
                                lastName,
                                phoneNumber,
                                residentialComplexName,
                                region,
                                bankName,
                                applicationStatus)
                );

        sortBankApplicationList(sortBy, sortDirection, bankApplications);

        return bankApplications.stream()
                .map(bankApplicationMapper::toBankApplicationResponse)
                .collect(Collectors.toList());
    }

    private void sortBankApplicationList(String sortBy, String sortDirection, List<BankApplication> bankApplications) {
        Comparator<BankApplication> comparator;
        if (sortBy != null) {
            if (sortBy.equals(CREATED_AT)) {
                comparator = Comparator.comparing(ba -> ba.getPartnerApplication().getCreatedAt());
            } else {
                comparator = Comparator.comparing(ba -> ba.getPartnerApplication().getUpdatedAt());
            }

            if (sortDirection != null && sortDirection.equals("DESC")) {
                comparator = comparator.reversed();
            }

            bankApplications.sort(comparator);
        }
    }

    private List<BankApplication> buildBorrowerApplications(PartnerApplicationRequest request, PartnerApplication partnerApplication) {
        List<UUID> creditProgramIds = request.getBankApplications().stream()
                .map(BankApplicationRequest::getCreditProgramId)
                .collect(Collectors.toList());
        List<UUID> existingBorrowerApplicationIds = request.getBankApplications().stream()
                .map(BankApplicationRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<UUID, CreditProgram> creditProgramMap = creditProgramRepository.findByIdInWithBank(creditProgramIds).stream()
                .collect(Collectors.toMap(CreditProgram::getId, Function.identity()));
        Map<UUID, BankApplication> existingBorrowerApplicationsMap = bankApplicationRepository.findAllById(existingBorrowerApplicationIds).stream()
                .collect(Collectors.toMap(BankApplication::getId, Function.identity()));
        return request.getBankApplications().stream()
                .map(borrowerRequest -> {
                    BankApplication existingBankApplication = existingBorrowerApplicationsMap.get(borrowerRequest.getId());
                    BankApplication updatedBankApplication = bankApplicationMapper.updateBankApplicationFromRequest(existingBankApplication, borrowerRequest);
                    updatedBankApplication.setPartnerApplication(partnerApplication);
                    updatedBankApplication.setCreditProgram(creditProgramMap.get(borrowerRequest.getCreditProgramId()));
                    return updatedBankApplication;
                })
                .collect(Collectors.toList());
    }


    private void checkPermission(Collection<? extends GrantedAuthority> authorities, PartnerApplication partnerApplication) {
        String currentUserToken = currentUserService.getCurrentUserToken();
        Integer organizationId = TokenExtractor.extractSdCurrentOrganizationId(currentUserToken);
        Integer sdId = TokenExtractor.extractSdId(currentUserToken);
        if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_ORGANIZATION)) &&
                !partnerApplication.getPartner().getSmartDealOrganizationId().equals(organizationId)) {

            throw new AccessDeniedException("organization_id: " + organizationId, PartnerApplication.class);
        }
        if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_OWN)) &&
                !partnerApplication.getCreatedBy().equals(sdId)) {
            throw new AccessDeniedException("sd_id: " + sdId, PartnerApplication.class);
        }
    }

    private PartnerApplication getPartnerApplication(PartnerApplicationRequest request) {
        RealEstate realEstate = realEstateService.findById(request.getRealEstateId());
        Partner partner = realEstate.getPartner();
        return partnerApplicationMapper.toPartnerApplication(request)
                .setPartner(partner)
                .setRealEstate(realEstate);
    }


    private PartnerApplication getPartnerApplicationById(UUID partnerApplicationId) {
        return partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, String.valueOf(partnerApplicationId)));
    }
}
