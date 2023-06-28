package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.NotificationService;
import pro.mbroker.app.util.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final byte FIRST_ELEMENT = 0;

    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final BankApplicationService bankApplicationService;
    private final BankApplicationRepository bankApplicationRepository;
    private final BorrowerDocumentRepository borrowerDocumentRepository;

    @Override //TODO обработать ситуацию, когда нет данных для отправки
    public NotificationBankLetterResponse getCustomerInfoForBankLetter(UUID bankApplicationId) {
        bankApplicationService.changeStatus(bankApplicationId, BankApplicationStatus.SENDING_TO_BANK);
        log.info("Начинаю процедуру получения информации для формирования письма");
        var customerInfoForBankLetter = bankApplicationRepository
                .getCustomerInfoForBankLetter(bankApplicationId, PageRequest.of(0, 1));
        log.info("Закончен процесс получения информации для формирования письма. Получено: {}",
                customerInfoForBankLetter.getContent());

        log.info("Начинаю процесс получения списка id документов для заемщика {}",
                customerInfoForBankLetter.getContent().get(FIRST_ELEMENT).getBorrowerId());
        var attachmentIds = customerInfoForBankLetter
                .stream()
                .flatMap(el -> borrowerDocumentRepository.getaAttachmentIds(el.getBorrowerId()).stream())
                .collect(Collectors.toList());
        log.info("Список id документов успешно сформирован {}", attachmentIds);

        log.info("Начинаю процесс получения списка документов для заемщика");
        var attachmentInfoList = convertMultipartFileToByteArray(attachmentIds);
        log.info("Список документов успешно сформирован");

        log.info("Начинаю процесс получения email адресов для отправки");
        var emails = bankApplicationRepository.getEmailsByBankApplicationId(bankApplicationId);
        log.info("Закончен процесс получения email адресов для отправки. Всего адресов {}", emails.size());

        var notificationBankLetterResponse = customerInfoForBankLetter.getContent()
                .get(FIRST_ELEMENT);
        notificationBankLetterResponse.setAttachmentInfo(attachmentInfoList);
        notificationBankLetterResponse.setEmails(emails);
        notificationBankLetterResponse.setCreditPurposeTypeName(
                notificationBankLetterResponse.convertCreditPurposeTypeToString(
                        Converter.convertStringListToEnumList(notificationBankLetterResponse.getCreditPurposeType(),
                                CreditPurposeType.class)));


        return notificationBankLetterResponse;

    }

    private List<AttachmentInfo> convertMultipartFileToByteArray(List<Long> attachmentIds) {
        List<AttachmentInfo> attachmentList = new ArrayList<>();
        log.info("Начинаю операцию по преобразованию файлов из БД");
        attachmentIds.forEach(id -> {
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
}
