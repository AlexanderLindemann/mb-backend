package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.NotificationService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        UUID borrowerId = customerInfoForBankLetter.getContent().get(FIRST_ELEMENT).getBorrowerId();
        log.info("Начинаю процесс получения списка id документов для заемщика {}", borrowerId);
        Set<Long> attachmentIds = Stream.concat(
                borrowerDocumentRepository.getAttachmentIds(bankApplicationId).stream(),
                borrowerDocumentRepository.getAttachmentsWithoutBankId(borrowerId).stream()
        ).collect(Collectors.toSet());
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
