package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.util.Converter;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final AttachmentService attachmentService;
    private final BorrowerProfileService borrowerProfileService;
    private final PartnerApplicationService partnerApplicationService;
    private final BorrowerDocumentService borrowerDocumentService;
    private final BorrowerDocumentMapper borrowerDocumentMapper;
    private final AttachmentControllerService attachmentControllerService;

    @Override
    public Long upload(MultipartFile file) {
        return attachmentService.upload(file).getId();
    }

    @Override
    public BorrowerDocumentResponse uploadDocument(MultipartFile file,
                                                   UUID borrowerProfileId,
                                                   DocumentType documentType,
                                                   UUID bankId) {
        BorrowerDocumentRequest borrowerDocumentRequest = new BorrowerDocumentRequest()
                .setBorrowerProfileId(borrowerProfileId)
                .setDocumentType(documentType);
        if (Objects.nonNull(bankId)) {
            borrowerDocumentRequest.setBankId(bankId);
        }
        BorrowerDocument borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest);
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerProfileId);
        partnerApplicationService.statusChanger(borrowerProfile.getPartnerApplication());
        Map<UUID, BorrowerProfile> borrowerProfileMap = borrowerProfile.getPartnerApplication().getBorrowerProfiles()
                .stream().collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
        return borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument)
                .setStatus(borrowerProfileMap.get(borrowerProfile.getId()).getBorrowerProfileStatus());
    }

    @Override
    public void deleteDocument(Long attachmentId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId);
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        return attachmentService.download(attachmentId);
    }

    @Override
    public MultipartFile downloadBase64(Long attachmentId) {
        return attachmentControllerService.download(attachmentId);
    }

}
