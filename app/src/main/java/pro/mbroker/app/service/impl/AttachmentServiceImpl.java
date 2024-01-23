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
import pro.smartdeal.ng.attachment.api.AttachmentRestApi;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRestApi attachmentService;
    private final BankRepository bankRepository;
    private final AttachmentRepository attachmentRepository;
    private final BorrowerDocumentRepository borrowerDocumentRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;

    @Override
    @Transactional
    public Attachment upload(MultipartFile file, Integer sdId) {
        AttachmentMeta upload = attachmentService.upload(file);
        return attachmentRepository.save(new Attachment()
                        .setId(upload.getId())
                        .setName(replaceFileNameDots(upload.getName()))
                        .setMimeType(upload.getMimeType())
                        .setSizeBytes(upload.getSizeBytes())
                        .setContentMd5(upload.getMd5Hash()))
                .setCreatedBy(sdId);
    }

    @Override
    @Transactional
    public MultipartFile download(Long attachmentId) {
        return getFileFromAttachmentService(attachmentId);
    }

    @Override
    @Transactional
    public BorrowerDocument uploadDocument(MultipartFile file, BorrowerDocumentRequest documentDto, Integer sdId) {
        Attachment attachment = upload(file, sdId);
        attachment.setCreatedBy(sdId);
        var borrowerProfile = borrowerProfileRepository.findById(documentDto.getBorrowerProfileId())
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, documentDto.getBorrowerProfileId()));
        BorrowerDocument borrowerDocument = new BorrowerDocument()
                .setAttachment(attachment)
                .setDocumentType(documentDto.getDocumentType())
                .setBorrowerProfile(borrowerProfile);
        if (Objects.nonNull(documentDto.getBankId())) {
            borrowerDocument.setBank(bankRepository.findById(documentDto.getBankId())
                    .orElseThrow(() -> new ItemNotFoundException(Bank.class, documentDto.getBankId())));
        }
        borrowerDocument.setCreatedBy(sdId);
        borrowerDocument.setUpdatedBy(sdId);
        return borrowerDocumentRepository.save(borrowerDocument);
    }

    @Override
    @Transactional
    public BorrowerDocument saveBorrowerDocument(Attachment attachment, BorrowerDocumentRequest documentDto) {
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

    public void markAttachmentAsDeleted(Long attachmentId, Integer sdId) {
        var attachment = attachmentRepository.findAttachmentById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        attachment.setActive(false);
        attachment.setUpdatedBy(sdId);
        attachmentRepository.save(attachment);
    }

    public void markAttachmentsAsDeleted(List<Long> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }
        attachmentRepository.setAttachmentsInactive(attachmentIds);
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
                        id,
                        attachment.getBytes(),
                        ourAttachment.getName(),
                        ourAttachment.getMimeType()
                ));
                log.info("Файл успешно преобразован и добавлен в список для отправки c id {} и именем {}",
                        id, ourAttachment.getName());
            } catch (IOException e) {
                log.error("Произошла ошибка при преобразовании файла к массиву байтов по причине {}",
                        e.getMessage());
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

    private String replaceFileNameDots(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        String fileNameWithoutExtension = lastDotIndex != -1 ? fileName.substring(0, lastDotIndex) : fileName;
        String extension = lastDotIndex != -1 ? fileName.substring(lastDotIndex) : "";

        return fileNameWithoutExtension.replace(".", "_") + extension;
    }
}
