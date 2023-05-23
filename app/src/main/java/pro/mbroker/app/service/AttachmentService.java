package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.Attachment;

public interface AttachmentService {
    Attachment upload(MultipartFile file);

    MultipartFile download(Long attachmentId);
}
