package pro.mbroker.app.service;

import org.springframework.data.domain.Page;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PartnerApplicationService {

    Page<PartnerApplication> getAllPartnerApplication(PartnerApplicationServiceRequest request);

    PartnerApplication createPartnerApplication(PartnerApplicationRequest request, Integer sdId);

    PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request, Integer sdId);

    PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication);

    List<PartnerApplicationResponse> buildPartnerApplicationResponse(List<PartnerApplication> partnerApplications);

    PartnerApplication getPartnerApplicationByIdCheckPermission(UUID partnerApplicationId);

    PartnerApplication getPartnerApplication(UUID partnerApplicationId);

    PartnerApplication enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request, Integer sdId);

    PartnerApplication disableBankApplication(UUID partnerApplicationId, UUID creditProgramId, Integer sdId);

    List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId);

    PartnerApplication changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId, Integer sdId);

    PartnerApplication getPartnerApplicationByAttachmentId(Long attachmentId);

    List<PartnerApplication> getPartnerApplicationByIds(List<UUID> ids);

    void saveAll(Collection<PartnerApplication> partnerApplications);

    PartnerApplication save(PartnerApplication partnerApplication);

    void changePartnerApplicationActiveStatus(UUID partnerApplicationId, boolean isActive, Integer sdId);

    List<PartnerApplication> findPartnerApplicationByPhoneNumber(String phoneNumber);
}
