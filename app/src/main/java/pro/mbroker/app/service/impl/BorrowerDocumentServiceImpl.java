package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.StatusService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowerDocumentServiceImpl implements BorrowerDocumentService {
    private final StatusService statusService;
    private final BorrowerDocumentRepository borrowerDocumentRepository;
    private final PartnerApplicationService partnerApplicationService;

    @Override
    @Transactional
    public void deleteDocumentByAttachmentId(Long attachmentId) {
        BorrowerDocument borrowerDocument = getBorrowerDocumentByAttachmentId(attachmentId);
        borrowerDocument.setActive(false);
        statusService.statusChanger(borrowerDocument.getBorrowerProfile().getPartnerApplication());
        PartnerApplication partnerApplication = borrowerDocument.getBorrowerProfile().getPartnerApplication();
        partnerApplicationService.save(partnerApplication);
    }

    @Override
    public BorrowerDocument saveBorrowerDocument(BorrowerDocument document, DocumentType documentType) {
        document.setDocumentType(documentType);
        return borrowerDocumentRepository.save(document);
    }

    @Override
    public void markDocumentsAsDeleted(List<Long> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }
        List<BorrowerDocument> borrowerDocuments =
                borrowerDocumentRepository.findAllByAttachmentIdIn(attachmentIds);
        if (Objects.nonNull(borrowerDocuments)) {
            borrowerDocumentRepository.setAttachmentsInactive(borrowerDocuments.stream()
                    .map(BorrowerDocument::getId)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public BorrowerDocument getBorrowerDocumentByAttachmentId(Long attachmentId) {
        return borrowerDocumentRepository.findByAttachmentId(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerDocument.class, attachmentId));
    }
}
