package pro.mbroker.app.service;

import org.springframework.data.domain.Page;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerFilter;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.PartnerApplication;
import pro.smartdeal.common.security.Permission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PartnerApplicationService {
    Page<PartnerApplication> getAllPartnerApplication(int page,
                                                      int size,
                                                      String sortBy,
                                                      String sortOrder,
                                                      LocalDateTime startDate,
                                                      LocalDateTime endDate,
                                                      Permission permission);

    PartnerApplication createPartnerApplication(PartnerApplicationRequest request);

    PartnerApplication updatePartnerApplication(UUID partnerApplicationId,
                                                PartnerApplicationRequest request,
                                                Permission permission);

    void deletePartnerApplication(UUID partnerApplicationId);

    PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication);

    PartnerApplication getPartnerApplicationByIdWithPermission(UUID partnerApplicationId, Permission permission);

    PartnerApplication getPartnerApplication(UUID partnerApplicationId);

    List<PartnerApplicationResponse> search(PartnerFilter filter, String sortBy, String sortDirection);

    PartnerApplication enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request);

    PartnerApplication disableBankApplication(UUID partnerApplicationId, UUID creditProgramId);

    List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId);

    PartnerApplication statusChanger(PartnerApplication partnerApplication);

    PartnerApplication changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId);
}
