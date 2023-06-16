package pro.mbroker.app.service;

import pro.mbroker.app.entity.BorrowerDocument;

public interface BorrowerDocumentService {

    BorrowerDocument getBorrowerDocumentByAttachmentId(Long attachmentId);

    void deleteDocumentByAttachmentId(Long attachmentId);
}
