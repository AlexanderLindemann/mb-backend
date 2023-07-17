package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentControllerService attachmentService;
    private final BankRepository bankRepository;
    private final AttachmentRepository attachmentRepository;
    private final BorrowerDocumentRepository borrowerDocumentRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;

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
        return getFileFromAttachmentService(attachmentId);
    }

    @Override
    @Transactional
    public BorrowerDocument uploadDocument(MultipartFile file,
                                           BorrowerDocumentRequest documentDto) {
        Attachment attachment = upload(file);
        var borrowerProfile = borrowerProfileRepository.findById(documentDto.getBorrowerProfileId())
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, documentDto.getBorrowerProfileId()));
        BorrowerDocument borrowerDocument = new BorrowerDocument().setAttachment(attachment)
                .setDocumentType(documentDto.getDocumentType()).setBorrowerProfile(borrowerProfile);
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

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(Long attachmentId) {
        MultipartFile file = getFileFromAttachmentService(attachmentId);
        InputStreamResource resource;
        if (file != null && file.getOriginalFilename() != null) {
            try {
                log.info("Начиная попытку преобразования файла {} в формат для скачивания",
                        file.getOriginalFilename());
                resource = new InputStreamResource(file.getInputStream());
                log.info("Файл успешно преобразован");
            } catch (IOException e) {
                log.error("Не удалось преобразовать файл: {} . Ошибка: {}",
                        file.getOriginalFilename(),
                        e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                    .body(resource);
        } else {
            throw new ItemNotFoundException(Attachment.class, attachmentId);
        }
    }

    public void markAttachmentAsDeleted(Long attachmentId) {
        var attachment = attachmentRepository.findAttachmentById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        attachment.setActive(false);
        attachmentRepository.save(attachment);
    }

    @Override
    public List<AttachmentInfo> getConvertedFiles(List<Long> attachmentsIds) {
        List<AttachmentInfo> attachmentList = new ArrayList<>();
        log.info("Начинаю операцию по преобразованию файлов из БД");
        attachmentsIds.forEach(id -> {
            try {
                Attachment ourAttachment = attachmentRepository.findAttachmentById(id)
                        .orElseThrow(() -> new ItemNotFoundException(Attachment.class, id));
                var attachment = attachmentService.download(id);
                attachmentList.add(new AttachmentInfo(
                        attachment.getBytes(),
                        ourAttachment.getName(),
                        ourAttachment.getMimeType()
                ));
                log.info("Файл успешно преобразован и добавлен в список для отправки");
            } catch (IOException e) {
                log.error("Произошла ошибка при преобразовании файла к массиву байтов");
                throw new RuntimeException("Ошибки при преобразовании файла", e.getCause());
            }
        });
        log.info("Завершена операция по преобразованию файлов из БД");
        return attachmentList;
    }

    private MultipartFile getFileFromAttachmentService(Long attachmentId) {
        Attachment attachment = attachmentRepository.findAttachmentById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        log.info("Начинаю попытку получить Attachment из сервиса Attachment с id {}", attachmentId);
        try {
            var file = attachmentService.download(attachment.getId());
            log.info("Файл {} успешно получен", file.getOriginalFilename());
            return file;
        } catch (Exception e) {
            log.error("При попытке получения файла из сервиса Attachment произошла ошибка. " +
                    "Будет возвращено null. Ошибка: {}", e.getMessage());
            return null;
        }
    }
}
