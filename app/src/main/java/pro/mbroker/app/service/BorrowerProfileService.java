package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.app.entity.BorrowerProfile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public interface BorrowerProfileService {

    BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request, Integer sdId);

    BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId);

    BorrowerProfile getBorrowerProfile(UUID borrowerProfileId);

    BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request, HttpServletRequest httpRequest, Integer sdId);

    void deleteBorrowerProfileById(UUID borrowerProfileId, Integer sdId);

    BorrowerResponse getBorrowersByBankApplicationId(UUID bankApplicationId);

    void updateBorrowerProfileField(UUID borrowerProfileId, Map<String, Object> fieldsMap);

    BorrowerProfile findByIdWithRealEstateVehicleAndEmployer(UUID borrowerProfileId);

    void updateBorrowerStatus(UUID borrowerProfileId, BorrowerProfileStatus status, Integer sdId);

    BorrowerProfile getFullBorrower(UUID borrowerProfileId);
}
