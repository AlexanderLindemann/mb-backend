package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentControllerService attachmentService;
    private final AttachmentRepository attachmentRepository;

    @Override
    public Attachment upload(MultipartFile file) {
        AttachmentMeta upload = attachmentService.upload(file);

        Attachment attachment = attachmentRepository.save(new Attachment()
                .setName(upload.getName())
                .setMimeType(upload.getMimeType())
                .setSizeBytes(upload.getSizeBytes())
                .setContentMd5(upload.getMd5Hash())
                .setExternalStorageId(upload.getId()));
        return attachment;
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        return attachmentService.download(attachment.getExternalStorageId());
    }
}
