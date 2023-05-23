package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.app.service.AttachmentService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final AttachmentService attachmentService;

    @Override
    public Long upload(MultipartFile file) {
        return attachmentService.upload(file).getExternalStorageId();
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        return attachmentService.download(attachmentId);
    }

}
