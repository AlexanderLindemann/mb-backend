package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.BorrowerDocument;

public interface AttachmentService {
    Long upload(MultipartFile file);

    MultipartFile download(Long attachmentId);

    BorrowerDocument uploadDocument(BorrowerDocumentRequest documentDto);

    Attachment getAttachmentById(Long attachmentId);
}
