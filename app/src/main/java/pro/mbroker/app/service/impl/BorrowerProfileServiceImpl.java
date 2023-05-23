package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerProfileServiceImpl implements BorrowerProfileService {
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final BankApplicationService bankApplicationService;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final PartnerApplicationService partnerApplicationService;

    @Override
    @Transactional
    public BorrowerResponse createOrUpdateBorrowerApplication(BorrowerRequest request) {
        BorrowerResponse borrowerResponse = new BorrowerResponse();
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(request.getId());
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        if (Objects.nonNull(request.getCoBorrower())) {
            borrowerResponse.setCoBorrower(saveCoBorrowers(partnerApplication, request.getCoBorrower()));
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            borrowerResponse.setMainBorrower(saveOrUpdateBorrowerProfile(partnerApplication, request.getMainBorrower()));
        }
        return getBorrowersByPartnerApplicationId(request.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(partnerApplicationId);
        BorrowerProfile mainBorrower;
        if (partnerApplication.getBankApplications().isEmpty()) {
            throw new ItemNotFoundException(BankApplication.class, partnerApplication.getId() +
                    "Could not find any bank applications with an associated client profile");
        } else {
            mainBorrower = partnerApplication.getBankApplications().get(0).getMainBorrower();
        }
        Map<UUID, BorrowerProfileResponse> coBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .filter(borrowerProfile -> !borrowerProfile.getId().equals(mainBorrower.getId()))
                .collect(Collectors.toMap(BorrowerProfile::getId, borrowerProfileMapper::toBorrowerProfileResponse));
        return new BorrowerResponse()
                .setMainBorrower(borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower))
                .setCoBorrower(new ArrayList<>(coBorrowerProfiles.values()));
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerProfile getBorrowerProfile(UUID borrowerProfileId) {
        return borrowerProfileRepository.findById(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    @Override
    @Transactional
    public BorrowerResponse createOrUpdateGenericBorrowerApplication(BorrowerRequest request) {
        BorrowerResponse borrowerResponse = new BorrowerResponse();
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(request.getId());
        if (Objects.nonNull(request.getCoBorrower())) {
            borrowerResponse.setCoBorrower(saveCoBorrowers(partnerApplication, request.getCoBorrower()));
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            borrowerResponse.setMainBorrower(saveOrUpdateBorrowerProfile(partnerApplication, request.getMainBorrower()));
        }
        return getBorrowersByPartnerApplicationId(request.getId());
    }

    private BorrowerProfileResponse saveOrUpdateBorrowerProfile(PartnerApplication partnerApplication, BorrowerProfileRequest borrower) {
        BorrowerProfile borrowerProfile;

        if (borrower.getId() != null) {
            borrowerProfile = borrowerProfileRepository.findById(borrower.getId())
                    .map(existingBorrowerProfile -> {
                        borrowerProfileMapper.updateBorrowerProfile(borrower, existingBorrowerProfile);
                        return existingBorrowerProfile;
                    })
                    .orElseGet(() -> {
                        BorrowerProfile newBorrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrower);
                        newBorrowerProfile.setPartnerApplication(partnerApplication);
                        return newBorrowerProfile;
                    });
        } else {
            borrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrower);
            borrowerProfile.setPartnerApplication(partnerApplication);
        }

        BorrowerProfile savedBorrowerProfile = borrowerProfileRepository.save(borrowerProfile);
        return borrowerProfileMapper.toBorrowerProfileResponse(savedBorrowerProfile);
    }

    private List<BorrowerProfileResponse> saveCoBorrowers(PartnerApplication partnerApplication, List<BorrowerProfileRequest> borrowers) {
        return borrowers.stream()
                .map(borrower -> saveOrUpdateBorrowerProfile(partnerApplication, borrower))
                .collect(Collectors.toList());
    }
}
