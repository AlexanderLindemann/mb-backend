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
    public Attachment upload(MultipartFile file) {
        AttachmentMeta upload = attachmentService.upload(file);

        Attachment attachment = attachmentRepository.save(new Attachment()
                .setId(upload.getId())
                .setName(upload.getName())
                .setMimeType(upload.getMimeType())
                .setSizeBytes(upload.getSizeBytes())
                .setContentMd5(upload.getMd5Hash()));
        return attachment;
    }

    @Override
    @Transactional
    public MultipartFile download(Long attachmentId) {
        Attachment attachment = attachmentRepository.findAttachmentById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        log.info("Начинаю попытку получить Attachment из сервиса Attachment с id {}", attachmentId);
        try {
            var file = attachmentService.download(attachment.getId());
            log.info("Файл {} успешно получен", file.getName());
            return file;
        } catch (Exception e) {
            log.error("При попытке получения файла из сервиса Attachment произошла ошибка. " +
                    "Будет возвращено null. Ошибка: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public BorrowerDocument uploadDocument(MultipartFile file,
                                           BorrowerDocumentRequest documentDto) {
        Attachment attachment = upload(file);
        BorrowerDocument borrowerDocument = new BorrowerDocument().setAttachment(attachment)
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
