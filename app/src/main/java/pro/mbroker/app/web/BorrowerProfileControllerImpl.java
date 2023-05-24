package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BorrowerApplicationController;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.service.BorrowerProfileService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowerProfileControllerImpl implements BorrowerApplicationController {
    private final BorrowerProfileService borrowerProfileService;


    @Override
    public BorrowerResponse createOrUpdateBorrowerApplication(BorrowerRequest request) {
        BorrowerResponse borrowerApplication = borrowerProfileService.createOrUpdateBorrowerApplication(request);
        return borrowerApplication;
    }

    @Override
    public BorrowerResponse createOrUpdateGenericBorrowerApplication(BorrowerRequest request) {
        return borrowerProfileService.createOrUpdateGenericBorrowerApplication(request);
    }

    @Override
    public BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId) {
        return borrowerProfileService.getBorrowersByPartnerApplicationId(partnerApplicationId);
    }
}
