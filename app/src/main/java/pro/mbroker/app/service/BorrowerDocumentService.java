package pro.mbroker.app.service;

import org.springframework.context.annotation.Lazy;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.BorrowerDocument;

@Lazy
public interface BorrowerDocumentService {

    BorrowerDocument getBorrowerDocumentByAttachmentId(Long attachmentId);

    void deleteDocumentByAttachmentId(Long attachmentId);

    BorrowerDocument saveBorrowerDocument(BorrowerDocument singForm, DocumentType documentType);
}
