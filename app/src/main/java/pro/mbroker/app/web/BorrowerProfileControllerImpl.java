package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BorrowerProfileController;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.service.BorrowerProfileService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowerProfileControllerImpl implements BorrowerProfileController {
    private final BorrowerProfileService borrowerProfileService;


    @Override
    public BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request) {
        return borrowerProfileService.createOrUpdateBorrowerProfile(request);
    }

    @Override
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request) {
        return borrowerProfileService.createOrUpdateGenericBorrowerProfile(request);
    }

    @Override
    public BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId) {
        return borrowerProfileService.getBorrowersByPartnerApplicationId(partnerApplicationId);
    }

    @Override
    public void deleteBorrowerProfileById(UUID borrowerProfileId) {
        borrowerProfileService.deleteBorrowerProfileById(borrowerProfileId);
    }
}
