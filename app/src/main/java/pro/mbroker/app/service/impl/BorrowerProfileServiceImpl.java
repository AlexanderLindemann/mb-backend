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
import pro.mbroker.app.exception.ItemConflictException;
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
    public BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(request.getId());
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        List<BorrowerProfile> borrowerProfilesToSave = new ArrayList<>();
        if (Objects.nonNull(request.getCoBorrower())) {
            List<BorrowerProfile> coBorrowerProfiles = request.getCoBorrower().stream()
                    .map(borrower -> prepareBorrowerProfile(partnerApplication, borrower))
                    .collect(Collectors.toList());
            borrowerProfilesToSave.addAll(coBorrowerProfiles);
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            BorrowerProfile mainBorrowerProfile = prepareBorrowerProfile(partnerApplication, request.getMainBorrower());
            borrowerProfilesToSave.add(mainBorrowerProfile);
        }
        borrowerProfileRepository.saveAll(borrowerProfilesToSave);
        borrowerProfileRepository.flush();
        return getBorrowersByBankApplicationId(bankApplication.getId());
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
                .filter(borrowerProfile -> !borrowerProfile.getId().equals(mainBorrower.getId()) && borrowerProfile.isActive())
                .collect(Collectors.toMap(BorrowerProfile::getId, borrowerProfileMapper::toBorrowerProfileResponse));

        List<BorrowerProfileResponse> sortedCoBorrowerProfiles = new ArrayList<>(coBorrowerProfiles.values());
        sortedCoBorrowerProfiles.sort(Comparator.comparing(BorrowerProfileResponse::getCreatedAt).reversed());

        return new BorrowerResponse()
                .setMainBorrower(borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower))
                .setCoBorrower(sortedCoBorrowerProfiles);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowersByBankApplicationId(UUID bankApplicationId) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        BorrowerProfile mainBorrower = bankApplication.getMainBorrower();
        Map<UUID, BorrowerProfileResponse> coBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .filter(borrowerProfile -> !borrowerProfile.getId().equals(mainBorrower.getId()) && borrowerProfile.isActive())
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
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(request.getId());
        List<BorrowerProfile> borrowerProfilesToSave = new ArrayList<>();
        if (Objects.nonNull(request.getCoBorrower())) {
            List<BorrowerProfile> coBorrowerProfiles = request.getCoBorrower().stream()
                    .map(borrower -> prepareBorrowerProfile(partnerApplication, borrower))
                    .collect(Collectors.toList());
            borrowerProfilesToSave.addAll(coBorrowerProfiles);
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            BorrowerProfile mainBorrowerProfile = prepareBorrowerProfile(partnerApplication, request.getMainBorrower());
            borrowerProfilesToSave.add(mainBorrowerProfile);
        }
        borrowerProfileRepository.saveAll(borrowerProfilesToSave);
        borrowerProfileRepository.flush();
        return getBorrowersByPartnerApplicationId(request.getId());
    }

    @Override
    public void deleteBorrowerProfileById(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = getBorrowerProfile(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        List<UUID> mainBorrowerIds = partnerApplication.getBankApplications().stream()
                .map(bankApplication ->
                        bankApplication.getMainBorrower().getId())
                .collect(Collectors.toList());
        if (!mainBorrowerIds.contains(borrowerProfile.getId())) {
            borrowerProfile.setActive(false);
            borrowerProfileRepository.save(borrowerProfile);
        } else {
            throw new ItemConflictException(BorrowerProfile.class, borrowerProfileId);
        }
    }

    private BorrowerProfile prepareBorrowerProfile(PartnerApplication partnerApplication, BorrowerProfileRequest borrower) {
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

        return borrowerProfile;
    }


}
