package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.NotificationService;

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
                .flatMap(el -> borrowerDocumentRepository.getaAttachmentIds(bankApplicationId).stream())
                .collect(Collectors.toList());
        log.info("Список id документов успешно сформирован {}", attachmentIds);

        log.info("Начинаю процесс получения email адресов для отправки");
        var emails = bankApplicationRepository.getEmailsByBankApplicationId(bankApplicationId);
        log.info("Закончен процесс получения email адресов для отправки. Всего адресов {} список: {}",
                emails.size(),
                emails);

        var notificationBankLetterResponse = customerInfoForBankLetter.getContent()
                .get(FIRST_ELEMENT);
        notificationBankLetterResponse.setAttachmentIds(attachmentIds);
        notificationBankLetterResponse.setEmails(emails);
        notificationBankLetterResponse.setCreditPurposeTypeName(
                notificationBankLetterResponse.getCreditPurposeType().getName());

        return notificationBankLetterResponse;

    }
}
