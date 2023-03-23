package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.ProfileAttachmentController;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;

@Component
@Transactional
@RequiredArgsConstructor
public class ProfileAttachmentControllerImpl implements ProfileAttachmentController {

    private final AttachmentControllerService attachmentService;

    @Override
    public void upload(Long profileId, MultipartFile file) {

    }

    @Override
    public void deleteAttachment(Long profileId, Long attachmentId) {

    }
}
