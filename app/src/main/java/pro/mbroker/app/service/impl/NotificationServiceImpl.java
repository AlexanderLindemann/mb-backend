package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.exception.DataNotFoundException;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.NotificationService;

import java.util.List;
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
    private final BorrowerProfileService borrowerProfileService;

    @Override
    public NotificationBankLetterResponse getCustomerInfoForBankLetter(UUID bankApplicationId) {
        bankApplicationService.changeStatus(bankApplicationId, BankApplicationStatus.SENDING_TO_BANK);
        NotificationBankLetterResponse notificationResponse = fetchNotificationData(bankApplicationId);
        enrichNotificationResponseWithData(notificationResponse, bankApplicationId);
        return notificationResponse;
    }

    private NotificationBankLetterResponse fetchNotificationData(UUID bankApplicationId) {
        log.info("Fetching information for letter formation");
        Page<NotificationBankLetterResponse> customerInfoPage = bankApplicationRepository
                .getCustomerInfoForBankLetter(bankApplicationId, PageRequest.of(0, 1));
        if (customerInfoPage.isEmpty()) {
            throw new DataNotFoundException("No customer information found for the given bank application ID");
        }
        return customerInfoPage.getContent().get(FIRST_ELEMENT);
    }

    private void enrichNotificationResponseWithData(NotificationBankLetterResponse response, UUID bankApplicationId) {
        UUID borrowerId = response.getBorrowerId();
        UUID partnerApplicationId = response.getPartnerApplicationId();
        BorrowerResponse borrowers = borrowerProfileService.getBorrowersByPartnerApplicationId(partnerApplicationId);
        List<UUID> concatenatedBorrowers = extractAllBorrowerIds(borrowers);
        log.info("Fetching document IDs for borrower {}", borrowerId);
        response.setAttachmentIds(borrowerDocumentRepository.getAttachments(bankApplicationId, concatenatedBorrowers));
        log.info("Fetching email addresses for sending");
        response.setEmails(bankApplicationRepository.getEmailsByBankApplicationId(bankApplicationId));
        response.setCreditPurposeTypeName(response.getCreditPurposeType().getName());
    }

    private List<UUID> extractAllBorrowerIds(BorrowerResponse borrowers) {
        return Stream.concat(
                        Stream.of(borrowers.getMainBorrower()),
                        borrowers.getCoBorrower().stream())
                .map(BorrowerProfileResponse::getId)
                .collect(Collectors.toList());
    }

}
