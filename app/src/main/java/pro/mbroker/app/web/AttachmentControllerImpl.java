package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.AttachmentRequest;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.util.List;
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
    private final BorrowerDocumentRepository borrowerDocumentRepository;

    @Override
    public Long upload(MultipartFile file) {
        return attachmentService.upload(file).getId();
    }

    @Override
    public BorrowerDocumentResponse uploadDocument(MultipartFile file,
                                                   UUID borrowerProfileId,
                                                   DocumentType documentType,
                                                   UUID bankId,
                                                   UUID bankApplicationId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerProfileId);
        List<BankApplication> bankApplications = borrowerProfileService.getBorrowerProfile(borrowerProfileId).getPartnerApplication().getBankApplications();
        if (bankApplicationId != null) {
            bankApplications = bankApplications.stream()
                    .filter(bankApplication -> bankApplication.getId().equals(bankApplicationId))
                    .collect(Collectors.toList());
        }
        BorrowerDocumentRequest borrowerDocumentRequest = new BorrowerDocumentRequest()
                .setBorrowerProfileId(borrowerProfileId)
                .setDocumentType(documentType);
        if (Objects.nonNull(bankId)) {
            borrowerDocumentRequest.setBankId(bankId);
        }
        BorrowerDocument borrowerDocument = null;
        if (isSpecialDocumentType(documentType)) {
            borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest);
            borrowerDocument.setBorrowerProfile(borrowerProfile);
            borrowerDocument.setBankApplication(null);
            borrowerDocumentRepository.save(borrowerDocument);
        } else {
            for (BankApplication bankApplication : bankApplications) {
                borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest);
                borrowerDocument.setBankApplication(bankApplication);
                borrowerDocument.setBorrowerProfile(borrowerProfile);
                borrowerDocumentRepository.save(borrowerDocument);
            }
        }
        partnerApplicationService.statusChanger(borrowerProfile.getPartnerApplication());
        Map<UUID, BorrowerProfile> borrowerProfileMap = borrowerProfile.getPartnerApplication().getBorrowerProfiles()
                .stream().collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
        return borrowerDocument == null ? null : borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument)
                .setStatus(borrowerProfileMap.get(borrowerProfile.getId()).getBorrowerProfileStatus());
    }

    @Override
    //TODO переделать логику. Убрать из этого метода borrowerDocumentService и сделать метод универсальным для аттачментов
    public void deleteDocument(Long attachmentId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId); //TODO Как только фронт переедет на deleteBorrowerDocument MB-285
        attachmentService.markAttachmentAsDeleted(attachmentId);
    }

    @Override
    public void deleteAttachment(AttachmentRequest attachmentRequest) {
        attachmentService.markAttachmentAsDeleted(attachmentRequest.getId());
        switch (attachmentRequest.getAttachmentType()) {
            case SIGNATURE_FORM:
                borrowerProfileService.deleteSignatureForm(attachmentRequest.getId());
                break;
            case BORROWER_DOCUMENT:
                borrowerDocumentService.deleteDocumentByAttachmentId(attachmentRequest.getId());
                break;
            case OTHER:
                break;
        }
    }

    @Override
    public List<AttachmentInfo> getConvertedFiles(List<Long> attachmentsIds) {
        return attachmentService.getConvertedFiles(attachmentsIds);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(Long attachmentId) {
        return attachmentService.downloadFile(attachmentId);
    }

    private boolean isSpecialDocumentType(DocumentType documentType) {
        return documentType == DocumentType.BORROWER_SNILS ||
                documentType == DocumentType.CERTIFIED_COPY_TK ||
                documentType == DocumentType.BORROWER_PASSPORT ||
                documentType == DocumentType.INCOME_CERTIFICATE;
    }

}
