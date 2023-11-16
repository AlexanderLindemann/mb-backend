package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.AccessDeniedException;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
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
            borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest);
            borrowerDocument.setBorrowerProfile(borrowerProfile);
            borrowerDocument.setBankApplication(null);
            borrowerProfile.getBorrowerDocument().add(borrowerDocument);
            borrowerDocumentRepository.save(borrowerDocument);
        } else {
            for (BankApplication bankApplication : bankApplications) {
                borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest);
                borrowerDocument.setBankApplication(bankApplication);
                borrowerDocument.setBorrowerProfile(borrowerProfile);
                borrowerProfile.getBorrowerDocument().add(borrowerDocument);
                borrowerDocumentRepository.save(borrowerDocument);
            }
        }
        statusService.statusChanger(borrowerProfile.getPartnerApplication());
        return borrowerDocument == null ? null : borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or " +
            "hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or " +
            "hasAuthority('MB_CABINET_ACCESS') or " +
            "hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    //TODO переделать логику. Убрать из этого метода borrowerDocumentService и сделать метод универсальным для аттачментов
    public void deleteDocument(Long attachmentId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId); //TODO Как только фронт переедет на deleteBorrowerDocument MB-285
        attachmentService.markAttachmentAsDeleted(attachmentId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or " +
            "hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or " +
            "hasAuthority('MB_CABINET_ACCESS') or " +
            "hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public void deleteAttachment(AttachmentRequest attachmentRequest) {
        PartnerApplication partnerApplication = partnerApplicationService
                .getPartnerApplicationByAttachmentId(attachmentRequest.getId())
                .orElseThrow(() -> new AccessDeniedException(attachmentRequest.getId(), PartnerApplication.class));
        partnerApplicationService.checkPermission(partnerApplication);
        attachmentService.markAttachmentAsDeleted(attachmentRequest.getId());
        switch (attachmentRequest.getAttachmentType()) {
            case SIGNATURE_FORM:
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
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or " +
            "hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or " +
            "hasAuthority('MB_CABINET_ACCESS') or " +
            "hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public ResponseEntity<InputStreamResource> downloadFile(Long attachmentId) {
        PartnerApplication partnerApplication = partnerApplicationService
                .getPartnerApplicationByAttachmentId(attachmentId)
                .orElseThrow(() -> new AccessDeniedException(attachmentId, PartnerApplication.class));
        partnerApplicationService.checkPermission(partnerApplication);
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
