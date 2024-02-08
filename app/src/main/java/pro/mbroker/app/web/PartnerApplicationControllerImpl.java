package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerApplicationController;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.LinkService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApplicationControllerImpl implements PartnerApplicationController {
    private final PartnerApplicationService partnerApplicationService;
    private final LinkService linkService;

    @Override
    public Page<PartnerApplicationResponse> getAllPartnerApplication(PartnerApplicationServiceRequest request) {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(request);
        List<PartnerApplicationResponse> responses = partnerApplicationService.buildPartnerApplicationResponse(partnerApplications.getContent());
        return new PageImpl<>(responses, partnerApplications.getPageable(), partnerApplications.getTotalElements());
    }

    @Override
    public PartnerApplicationResponse getPartnerApplicationById(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplicationByIdCheckPermission(partnerApplicationId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse createPartnerApplication(PartnerApplicationRequest request, Integer sdId) {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(request, sdId);
        linkService.addLinksByProfiles(partnerApplication.getBorrowerProfiles(), request.getPrefixLink());
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request, Integer sdId) {
        PartnerApplication partnerApplication = partnerApplicationService.updatePartnerApplication(partnerApplicationId, request, sdId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public void changePartnerApplicationActiveStatus(UUID partnerApplicationId, boolean isActive, Integer sdId) {
        partnerApplicationService.changePartnerApplicationActiveStatus(partnerApplicationId, isActive, sdId);
    }

    @Override
    public PartnerApplicationResponse enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request, Integer sdId) {
        PartnerApplication partnerApplication = partnerApplicationService.enableBankApplication(partnerApplicationId, request, sdId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse disableBankApplication(UUID partnerApplicationId, UUID creditProgramId, Integer sdId) {
        PartnerApplication partnerApplication = partnerApplicationService.disableBankApplication(partnerApplicationId, creditProgramId, sdId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId) {
        return partnerApplicationService.getRequiredDocuments(partnerApplicationId);
    }

    @Override
    public PartnerApplicationResponse changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId, Integer sdId) {
        PartnerApplication partnerApplication = partnerApplicationService.changeMainBorrowerByPartnerApplication(partnerApplicationId, newMainBorrowerId, sdId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse getPartnerApplicationByAttachmentId(Long attachmentId) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplicationByAttachmentId(attachmentId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public List<PartnerApplicationResponse> findPartnerApplicationByPhoneNumber(String phoneNumber) {
        List<PartnerApplication> partnerApplications = partnerApplicationService.findPartnerApplicationByPhoneNumber(phoneNumber);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplications);
    }
}
