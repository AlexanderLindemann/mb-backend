package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BorrowerProfileUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.UUID;

public interface BorrowerProfileService {

    BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request);

    BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId);

    BorrowerProfile getBorrowerProfile(UUID borrowerProfileId);

    BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request);

    void deleteBorrowerProfileById(UUID borrowerProfileId);

    BorrowerResponse getBorrowersByBankApplicationId(UUID bankApplicationId);

    void updateBorrowerProfileField(UUID borrowerProfileId, BorrowerProfileUpdateRequest updateRequest);

    BorrowerProfile findByIdWithRealEstateVehicleAndEmployer(UUID borrowerProfileId);

    void deleteSignatureForm(Long id);

    void updateBorrowerStatus (UUID borrowerProfileId, BorrowerProfileStatus status);
}
