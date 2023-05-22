package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerProfileServiceImpl implements BorrowerProfileService {
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final BankApplicationService bankApplicationService;
    private final BorrowerProfileMapper borrowerProfileMapper;

    @Override
    public BorrowerResponse createOrUpdateBorrowerApplication(BorrowerRequest request) {
        BorrowerResponse borrowerResponse = new BorrowerResponse();

        BankApplication bankApplication = bankApplicationService.getBankApplicationById(request.getBankApplicationId());
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        if (Objects.nonNull(request.getCoBorrower())) {
            borrowerResponse.setCoBorrower(saveCoBorrower(partnerApplication, request.getCoBorrower()));
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            borrowerResponse.setMainBorrower(saveMainBorrower(partnerApplication, request.getMainBorrower()));
        }
        return getBorrowersByBankApplicationId(request.getBankApplicationId());
    }

    @Override
    public BorrowerResponse getBorrowersByBankApplicationId(UUID bankApplicationId) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        BorrowerProfile mainBorrower = bankApplication.getMainBorrower();

        Map<UUID, BorrowerProfile> coBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .filter(borrowerProfile -> !borrowerProfile.getId().equals(mainBorrower.getId())) // Exclude main borrower
                .collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));

        BorrowerProfileResponse borrowerProfileResponse = borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower);

        List<BorrowerProfileResponse> coBorrowersResponse = coBorrowerProfiles.values().stream()
                .map(borrowerProfileMapper::toBorrowerProfileResponse)
                .collect(Collectors.toList());

        return new BorrowerResponse()
                .setBankApplicationId(bankApplicationId)
                .setMainBorrower(borrowerProfileResponse)
                .setCoBorrower(coBorrowersResponse);
    }

    @Override
    public BorrowerProfile getBorrowerProfile(UUID borrowerProfileId) {
        return borrowerProfileRepository.findById(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    private List<BorrowerProfileResponse> saveCoBorrower(PartnerApplication partnerApplication, List<BorrowerProfileRequest> borrowers) {
        List<BorrowerProfile> borrowerProfiles = borrowers.stream()
                .map(profile -> {
                    BorrowerProfile borrowerProfile;

                    if (profile.getId() != null) {
                        Optional<BorrowerProfile> optionalBorrowerProfile = borrowerProfileRepository.findById(profile.getId());
                        if (optionalBorrowerProfile.isPresent()) {
                            borrowerProfile = optionalBorrowerProfile.get();
                            borrowerProfileMapper.updateBorrowerProfile(profile, borrowerProfile);
                            return borrowerProfile;
                        }
                    }

                    borrowerProfile = borrowerProfileMapper.toBorrowerProfile(profile);
                    borrowerProfile.setPartnerApplication(partnerApplication);
                    return borrowerProfile;

                })
                .collect(Collectors.toList());

        List<BorrowerProfile> profiles = borrowerProfileRepository.saveAll(borrowerProfiles);

        return profiles.stream()
                .map(borrowerProfileMapper::toBorrowerProfileResponse)
                .collect(Collectors.toList());
    }

    private BorrowerProfileResponse saveMainBorrower(PartnerApplication partnerApplication, BorrowerProfileRequest borrower) {
        BorrowerProfile borrowerProfile;
        if (Objects.nonNull(borrower.getId())) {
            borrowerProfile = borrowerProfileRepository.findById(borrower.getId())
                    .orElseThrow(() -> new ItemNotFoundException(BorrowerProfileRequest.class, borrower.getId()));
            borrowerProfileMapper.updateBorrowerProfile(borrower, borrowerProfile);
        } else {
            borrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrower);
            borrowerProfile.setPartnerApplication(partnerApplication);
        }

        BorrowerProfile profile = borrowerProfileRepository.save(borrowerProfile);
        return borrowerProfileMapper.toBorrowerProfileResponse(profile);
    }
}
