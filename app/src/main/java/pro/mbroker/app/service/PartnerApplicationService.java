package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.enums.ApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;
import java.util.UUID;

public interface PartnerApplicationService {
    List<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder);

    PartnerApplication createPartnerApplication(PartnerApplicationRequest request);

    PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request);

    void deletePartnerApplication(UUID partnerApplicationId);

    PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication);

    PartnerApplication getPartnerApplicationByIdWithPermission(UUID partnerApplicationId);

    PartnerApplication getPartnerApplication(UUID partnerApplicationId);

    List<BankApplicationResponse> search(String firstName,
                                         String middleName,
                                         String lastName,
                                         String phoneNumber,
                                         String residentialComplexName,
                                         RegionType region,
                                         String bankName,
                                         ApplicationStatus applicationStatus,
                                         String sortBy,
                                         String sortDirection);
}
