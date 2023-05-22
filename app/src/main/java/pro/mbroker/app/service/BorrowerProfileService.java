package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.UUID;

public interface BorrowerProfileService {

    BorrowerResponse createOrUpdateBorrowerApplication(BorrowerRequest request);

    BorrowerResponse getBorrowersByBankApplicationId(UUID bankApplicationId);

    BorrowerProfile getBorrowerProfile(UUID borrowerProfileId);
}
