package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.enums.ApplicationStatus;
import pro.mbroker.app.entity.*;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.repository.*;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.util.Pagination;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationServiceImpl implements PartnerApplicationService {
    private final PartnerRepository partnerRepository;
    private final CreditProgramRepository creditProgramRepository;
    private final CurrentUserService currentUserService;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final BorrowerApplicationRepository borrowerApplicationRepository;
    private final RealEstateRepository realEstateRepository;
    private final PartnerApplicationMapper partnerApplicationMapper;

    @Override
    public List<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        log.info("Getting all partner applications with pagination: page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);

        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        Partner partner = getPartnerByOrganizationId();

        List<PartnerApplication> partnerApplicationPage = partnerApplicationRepository.findAllByPartner(partner, pageable);
        log.info("Found {} partner applications for organization ID: {}", partnerApplicationPage.size(), partner.getSmartDealOrganizationId());

        return partnerApplicationPage;
    }

    @Override
    @Transactional
    public PartnerApplication createPartnerApplication(PartnerApplicationRequest request) {
        RealEstate realEstate = realEstateRepository.findById(request.getRealEstateId())
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, request.getRealEstateId()));
        Partner partner = realEstate.getPartner();
        PartnerApplication partnerApplication = partnerApplicationMapper.toPartnerApplication(request)
                .setPartner(partner)
                .setRealEstate(realEstate);

        List<BorrowerApplication> borrowerApplications = buildBorrowerApplications(request, partnerApplication);
        partnerApplication.setBorrowerApplications(borrowerApplications);
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    @Transactional
    public PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication existingPartnerApplication = partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, partnerApplicationId));

        RealEstate realEstate = realEstateRepository.findById(request.getRealEstateId())
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, request.getRealEstateId()));
        Partner partner = realEstate.getPartner();

        PartnerApplication updatedPartnerApplication = partnerApplicationMapper.toPartnerApplication(request);
        updatedPartnerApplication.setPartner(partner);
        updatedPartnerApplication.setRealEstate(realEstate);
        updatedPartnerApplication.setId(existingPartnerApplication.getId());

        List<BorrowerApplication> updatedBorrowerApplications = buildBorrowerApplications(request, updatedPartnerApplication);
        updatedPartnerApplication.setBorrowerApplications(updatedBorrowerApplications);
        partnerApplicationMapper.updatePartnerApplication(updatedPartnerApplication, existingPartnerApplication);
        return partnerApplicationRepository.save(existingPartnerApplication);
    }

    private List<BorrowerApplication> buildBorrowerApplications(PartnerApplicationRequest request, PartnerApplication partnerApplication) {
        List<UUID> creditProgramIds = new ArrayList<>();
        List<UUID> existingBorrowerApplicationIds = new ArrayList<>();
        request.getBorrowerApplications().forEach(borrowerRequest -> {
            creditProgramIds.add(borrowerRequest.getCreditProgramId());
            if (borrowerRequest.getId() != null) {
                existingBorrowerApplicationIds.add(borrowerRequest.getId());
            }
        });

        Map<UUID, CreditProgram> creditProgramMap = creditProgramRepository.findByIdInWithBank(creditProgramIds).stream()
                .collect(Collectors.toMap(CreditProgram::getId, Function.identity()));
        Map<UUID, BorrowerApplication> existingBorrowerApplicationsMap = borrowerApplicationRepository.findAllById(existingBorrowerApplicationIds).stream()
                .collect(Collectors.toMap(BorrowerApplication::getId, Function.identity()));

        return request.getBorrowerApplications().stream()
                .map(borrowerRequest -> {
                    BorrowerApplication borrower = existingBorrowerApplicationsMap.get(borrowerRequest.getId());
                    if (borrower == null) {
                        borrower = new BorrowerApplication();
                    }
                    borrower.setApplicationStatus(ApplicationStatus.UPLOADING_DOCUMENTS);
                    borrower.setPartnerApplication(partnerApplication);
                    borrower.setCreditProgram(creditProgramMap.get(borrowerRequest.getCreditProgramId()));
                    borrower.setMonthlyPayment(borrowerRequest.getMonthlyPayment());
                    borrower.setDownPayment(borrowerRequest.getDownPayment());
                    borrower.setMonthCreditTerm(borrowerRequest.getMonthCreditTerm());
                    borrower.setOverpayment(borrowerRequest.getOverpayment());
                    return borrower;
                })
                .collect(Collectors.toList());
    }

    private Partner getPartnerByOrganizationId() {
        String currentUserToken = currentUserService.getCurrentUserToken();
        int organizationId = TokenExtractor.extractSdCurrentOrganizationId(currentUserToken);

        log.info("Retrieving partner by organization ID: {}", organizationId);
        return partnerRepository.findBySmartDealOrganizationId(organizationId)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, String.valueOf(organizationId)));
    }
}