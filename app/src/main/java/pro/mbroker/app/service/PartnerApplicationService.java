package pro.mbroker.app.service;

import org.springframework.data.domain.Page;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.PartnerApplication;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerApplicationService {
    Page<PartnerApplication> getAllPartnerApplication(int page,
                                                      int size,
                                                      String sortBy,
                                                      String sortOrder,
                                                      LocalDateTime startDate,
                                                      LocalDateTime endDate,
                                                      String phoneNumber,
                                                      String fullName,
                                                      Integer applicationNumber,
                                                      UUID realEstateId,
                                                      RegionType region,
                                                      UUID bankId,
                                                      BankApplicationStatus applicationStatus,
                                                      Boolean isActive);

    PartnerApplication createPartnerApplication(PartnerApplicationRequest request);

    PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request);

    PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication);

    List<PartnerApplicationResponse> buildPartnerApplicationResponse(List<PartnerApplication> partnerApplications);

    PartnerApplication getPartnerApplicationByIdCheckPermission(UUID partnerApplicationId);

    PartnerApplication getPartnerApplication(UUID partnerApplicationId);

    PartnerApplication enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request);

    PartnerApplication disableBankApplication(UUID partnerApplicationId, UUID creditProgramId);

    List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId);

    PartnerApplication changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId);

    Optional<PartnerApplication> getPartnerApplicationByAttachmentId(Long attachmentId);

    void checkPermission(PartnerApplication partnerApplication);

    List<PartnerApplication> getPartnerApplicationByIds(List<UUID> ids);

    void saveAll(Collection<PartnerApplication> partnerApplications);

    PartnerApplication save(PartnerApplication partnerApplication);

    void changePartnerApplicationActiveStatus(UUID partnerApplicationId, boolean isActive);
}
