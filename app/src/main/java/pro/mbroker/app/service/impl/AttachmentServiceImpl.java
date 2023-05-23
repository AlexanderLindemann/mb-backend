package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.time.ZonedDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentControllerService attachmentService;
    private final BankRepository bankRepository;
    private final AttachmentRepository attachmentRepository;
    private final BorrowerDocumentRepository borrowerDocumentRepository;

    @Override
    @Transactional
    public Long upload(MultipartFile file) {
        AttachmentMeta upload = attachmentService.upload(file);
        Attachment attachment = attachmentRepository.save(new Attachment()
                .setCreatedAt(ZonedDateTime.now())
                .setName(upload.getName())
                .setMimeType(upload.getMimeType())
                .setSizeBytes(upload.getSizeBytes())
                .setContentMd5(upload.getMd5Hash())
                .setExternalStorageId(upload.getId()));
        return attachment.getId();
    }

    @Override
    @Transactional
    public MultipartFile download(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        return attachmentService.download(attachment.getExternalStorageId());
    }

    @Override
    @Transactional
    public BorrowerDocument uploadDocument(BorrowerDocumentRequest documentDto) {
        Long attachmentId = upload(documentDto.getMultipartFile());
        BorrowerDocument borrowerDocument = new BorrowerDocument().setAttachmentId(attachmentId)
                .setDocumentType(documentDto.getDocumentType());
        if (Objects.nonNull(documentDto.getBankId())) {
            borrowerDocument.setBank(bankRepository.findById(documentDto.getBankId())
                    .orElseThrow(() -> new ItemNotFoundException(Bank.class, documentDto.getBankId())));
        }
        return borrowerDocumentRepository.save(borrowerDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public Attachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
    }
}
