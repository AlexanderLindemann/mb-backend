package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Long upload(MultipartFile file);

    MultipartFile download(Long attachmentId);
}
