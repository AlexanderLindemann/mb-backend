package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.*;
import pro.mbroker.app.exception.AccessDeniedException;
import pro.mbroker.app.exception.ItemConflictException;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.mapper.MortgageCalculationMapper;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerProfileRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationServiceImpl implements PartnerApplicationService {

    private static final String CREATED_AT = "createdAt";

    private final CalculatorService calculatorService;
    private final CurrentUserService currentUserService;
    private final PartnerService partnerService;
    private final RealEstateService realEstateService;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final BankApplicationRepository bankApplicationRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final PartnerApplicationMapper partnerApplicationMapper;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final BankApplicationMapper bankApplicationMapper;
    private final MortgageCalculationMapper mortgageCalculationMapper;


    @Override
    @Transactional(readOnly = true)
    public List<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        log.info("Getting all partner applications with pagination: page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);
        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        List<PartnerApplication> result = new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_ADMIN_ACCESS))) {
            result = partnerApplicationRepository.findAllByIsActiveTrue(pageable);
        } else if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_ORGANIZATION))) {
            UUID partnerId = partnerService.getCurrentPartner().getId();
            result = partnerApplicationRepository.findAllIsActiveByPartnerId(partnerId, pageable);
        } else if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_OWN))) {
            Integer createdBy = TokenExtractor.extractSdId(currentUserService.getCurrentUserToken());
            result = partnerApplicationRepository.findAllByCreatedByAndIsActiveTrue(createdBy, pageable);
        }
        return result;
    }

    @Override
    @Transactional
    public PartnerApplication createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = getPartnerApplication(request);
        List<BankApplication> bankApplications = buildBorrowerApplications(request.getBankApplications(), partnerApplication);
        partnerApplication.setBankApplications(bankApplications);
        updateMainBorrower(partnerApplication, request.getMainBorrower());
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    @Transactional
    public PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication existingPartnerApplication = getPartnerApplicationByIdWithPermission(partnerApplicationId);
        removeDuplicateBankApplications(existingPartnerApplication);
        partnerApplicationMapper.updatePartnerApplicationFromRequest(request, existingPartnerApplication);
        mortgageCalculationMapper.updateMortgageCalculationFromRequest(request.getMortgageCalculation(), existingPartnerApplication.getMortgageCalculation());
        existingPartnerApplication.setRealEstate(realEstateService.findById(request.getRealEstateId()));
        List<BankApplication> updatedBorrowerApplications = buildBorrowerApplications(request.getBankApplications(), existingPartnerApplication);
        existingPartnerApplication.setBankApplications(updatedBorrowerApplications);
        updateMainBorrower(existingPartnerApplication, request.getMainBorrower());
        return partnerApplicationRepository.save(existingPartnerApplication);
    }

    @Transactional
    public void removeDuplicateBankApplications(PartnerApplication partnerApplication) {
        List<BankApplication> bankApplications = partnerApplication.getBankApplications();
        Map<UUID, BankApplication> uniqueBankApplicationsMap = bankApplications.stream()
                .collect(Collectors.toMap(
                        ba -> ba.getCreditProgram().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing));
        List<BankApplication> duplicateBankApplications = new ArrayList<>(bankApplications);
        duplicateBankApplications.removeAll(uniqueBankApplicationsMap.values());
        bankApplicationRepository.deleteAll(duplicateBankApplications);
        partnerApplication.setBankApplications(new ArrayList<>(uniqueBankApplicationsMap.values()));
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
        List<BankApplication> activeBankApplications = partnerApplication.getBankApplications().stream()
                .filter(BankApplication::isActive)
                .collect(Collectors.toList());
        Map<UUID, BorrowerProfile> borrowerProfileMap = partnerApplication.getBorrowerProfiles().stream()
                .collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
        List<BankApplicationResponse> activeBankApplicationResponses = new ArrayList<>();
        for (BankApplication bankApplication : activeBankApplications) {
            BankApplicationResponse bankApplicationResponse = bankApplicationMapper.toBankApplicationResponse(bankApplication);
            BigDecimal mortgageSum = calculatorService.getMortgageSum(bankApplication.getRealEstatePrice(), bankApplication.getDownPayment());
            bankApplicationResponse.setMortgageSum(mortgageSum);
            BorrowerProfile mainBorrower = bankApplication.getMainBorrower();
            UUID mainBorrowerId = mainBorrower != null ? mainBorrower.getId() : null;
            bankApplicationResponse.setCoBorrowers(borrowerProfileMap.values().stream()
                    .filter(borrowerProfile -> mainBorrower == null || !borrowerProfile.getId().equals(mainBorrowerId))
                    .map(borrowerProfileMapper::toBorrowerProfileResponse)
                    .collect(Collectors.toList()));
            activeBankApplicationResponses.add(bankApplicationResponse);
        }
        response.setBankApplications(activeBankApplicationResponses);
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
                                                BankApplicationStatus applicationStatus,
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

    @Override
    public PartnerApplication enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        BankApplication currentBankApplication = currentBankApplications.get(request.getCreditProgramId());
        if (currentBankApplication != null) {
            bankApplicationMapper.updateBankApplicationFromRequest(currentBankApplication, request);
        } else {
            BankApplication newBankApplication = bankApplicationMapper.toBankApplication(request);
            newBankApplication.setPartnerApplication(partnerApplication);
            newBankApplication.setMainBorrower(getBorrowerProfile(request.getMainBorrowerId()));
            currentBankApplications.put(request.getCreditProgramId(), newBankApplication);
        }
        partnerApplication.setBankApplications(new ArrayList<>(currentBankApplications.values()));
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    public PartnerApplication disableBankApplication(UUID partnerApplicationId, UUID creditProgramId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        BankApplication disabledBankApplication = currentBankApplications.get(creditProgramId);
        if (disabledBankApplication == null) {
            throw new ItemConflictException(CreditProgram.class, "This PartnerApplication with" + partnerApplicationId + " does not have such a credit program id: " + creditProgramId);
        }
        disabledBankApplication.setActive(false);
        partnerApplication.setBankApplications(new ArrayList<>(currentBankApplications.values()));
        return partnerApplicationRepository.save(partnerApplication);
    }

    private void updateMainBorrower(PartnerApplication partnerApplication, BorrowerProfileRequest borrowerProfileRequest) {
        BorrowerProfile borrowerProfile;
        if (borrowerProfileRequest.getId() != null) {
            borrowerProfile = getBorrowerProfile(borrowerProfileRequest.getId());
            borrowerProfileMapper.updateBorrowerProfile(borrowerProfileRequest, borrowerProfile);
        } else {
            if (!partnerApplication.getBorrowerProfiles().isEmpty()) {
                throw new ItemConflictException(BorrowerProfile.class,
                        "The PartnerApplication id: " + partnerApplication.getId() + " already has a mainBorrower. You need to specify the ID of the existing mainBorrower in the request");
            }
            borrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrowerProfileRequest);
            partnerApplication.getBorrowerProfiles().add(borrowerProfile);
        }
        borrowerProfile.setPartnerApplication(partnerApplication);
        partnerApplication.getBankApplications().forEach(app -> app.setMainBorrower(borrowerProfile));
    }

    private BorrowerProfile getBorrowerProfile(UUID borrowerProfileId) {
        return borrowerProfileRepository.findById(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    private List<BankApplication> buildBorrowerApplications(List<BankApplicationRequest> requests, PartnerApplication partnerApplication) {
        List<UUID> updateCreditProgramIds = requests.stream()
                .map(BankApplicationRequest::getCreditProgramId)
                .collect(Collectors.toList());
        MortgageCalculation mortgageCalculation = partnerApplication.getMortgageCalculation();
        if (mortgageCalculation.getIsMaternalCapital() == null) {
            mortgageCalculation.setIsMaternalCapital(false);
        }
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        requests.forEach(bankApplicationRequest -> {
            BankApplication currentBankApplication = currentBankApplications.get(bankApplicationRequest.getCreditProgramId());
            if (currentBankApplication != null) {
                bankApplicationMapper.updateBankApplicationFromRequest(currentBankApplication, bankApplicationRequest);
            } else {
                BankApplication newBankApplication = bankApplicationMapper.toBankApplication(bankApplicationRequest);
                newBankApplication.setPartnerApplication(partnerApplication);
                currentBankApplications.put(bankApplicationRequest.getCreditProgramId(), newBankApplication);
            }
        });
        currentBankApplications.forEach((key, value) -> {
            if (!updateCreditProgramIds.contains(key)) {
                value.setActive(false);
            }
        });
        return new ArrayList<>(currentBankApplications.values());
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
                .setRealEstate(realEstate)
                .setMortgageCalculation(mortgageCalculationMapper.toMortgageCalculation(request.getMortgageCalculation()));
    }

    private PartnerApplication getPartnerApplicationById(UUID partnerApplicationId) {
        return partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, String.valueOf(partnerApplicationId)));
    }
}
