package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.AttachmentRequest;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.dto.response.StorageResponse;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.FileStorage;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
import pro.mbroker.app.mapper.StorageMapper;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.StatusService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final AttachmentService attachmentService;
    private final BorrowerProfileService borrowerProfileService;
    private final BankApplicationService bankApplicationService;
    private final PartnerApplicationService partnerApplicationService;
    private final BorrowerDocumentService borrowerDocumentService;
    private final StatusService statusService;
    private final BorrowerDocumentMapper borrowerDocumentMapper;
    private final StorageMapper storageMapper;
    private final BorrowerDocumentRepository borrowerDocumentRepository;

    @Override
    public StorageResponse upload(MultipartFile file, Integer sdId) {
        FileStorage fileStorage = attachmentService.uploadS3(file, sdId);
        StorageResponse storageResponse = storageMapper.toStorageResponse(fileStorage);
        storageResponse.setUrl(attachmentService.getSignedUrl(fileStorage.getObjectKey()));
        return storageResponse;
    }

    @Override
    public BorrowerDocumentResponse uploadDocument(MultipartFile file,
                                                   UUID borrowerProfileId,
                                                   DocumentType documentType,
                                                   UUID bankId,
                                                   UUID bankApplicationId,
                                                   Integer sdId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerProfileId);
        List<BankApplication> bankApplications = bankApplicationService.getBankApplicationByBorrowerId(borrowerProfileId);
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
            borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest, sdId);
            borrowerDocument.setBorrowerProfile(borrowerProfile);
            borrowerDocument.setBankApplication(null);
            borrowerProfile.getBorrowerDocument().add(borrowerDocument);
            borrowerDocumentRepository.save(borrowerDocument);
        } else {
            for (BankApplication bankApplication : bankApplications) {
                borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest, sdId);
                borrowerDocument.setBankApplication(bankApplication);
                borrowerDocument.setBorrowerProfile(borrowerProfile);
                borrowerProfile.getBorrowerDocument().add(borrowerDocument);
                borrowerDocumentRepository.save(borrowerDocument);
            }
        }
        statusService.statusChanger(borrowerProfile.getPartnerApplication());
        partnerApplicationService.save(borrowerProfile.getPartnerApplication());
        return borrowerDocument == null ? null : borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument);
    }

    @Override
    public void deleteDocument(Long attachmentId, Integer sdId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId, sdId); //TODO Как только фронт переедет на deleteBorrowerDocument MB-285
        attachmentService.markAttachmentAsDeleted(attachmentId, sdId);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteAttachment(AttachmentRequest attachmentRequest, Integer sdId) {
        try {
            attachmentService.markAttachmentAsDeleted(attachmentRequest.getId(), sdId);
            switch (attachmentRequest.getAttachmentType()) {
                case SIGNATURE_FORM:
                case BORROWER_DOCUMENT:
                    borrowerDocumentService.deleteDocumentByAttachmentId(attachmentRequest.getId(), sdId);
                    break;
                case OTHER:
                    break;
            }
            return ResponseEntity.ok().build();
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
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
                documentType == DocumentType.FOREIGN_PASSPORT_OR_ID_CARD ||
                documentType == DocumentType.MILITARY_ID ||
                documentType == DocumentType.PENSION_CERTIFICATE ||
                documentType == DocumentType.EGRUL_EGRIP_EXTRACT ||
                documentType == DocumentType.OTHER_DOCUMENTS ||
                documentType == DocumentType.GENERATED_FORM ||
                documentType == DocumentType.GENERATED_SIGNATURE_FORM ||
                documentType == DocumentType.SIGNATURE_FORM ||
                documentType == DocumentType.INCOME_CERTIFICATE;
    }
}
