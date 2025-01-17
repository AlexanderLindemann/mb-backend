package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.notification.NotificationPartnerLetterRequest;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.dto.response.PartnerContactResponse;
import pro.mbroker.api.dto.response.notification.NotificationBankLetterResponse;
import pro.mbroker.api.dto.response.notification.NotificationPartnerLetterResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.NotificationTrigger;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.MortgageCalculation;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.DataNotFoundException;
import pro.mbroker.app.mapper.PartnerContactMapper;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.NotificationService;
import pro.mbroker.app.service.StatusService;
import pro.mbroker.app.util.Converter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final BankApplicationService bankApplicationService;
    private final BorrowerProfileService borrowerProfileService;
    private final StatusService statusService;
    private final BankApplicationRepository bankApplicationRepository;
    private final PartnerContactMapper partnerContactMapper;

    @Override
    @Transactional
    public NotificationBankLetterResponse getCustomerInfoForBankLetter(UUID bankApplicationId) {
        NotificationBankLetterResponse notificationResponse = fetchNotificationData(bankApplicationId);
        enrichNotificationResponseWithData(notificationResponse, bankApplicationId);
        bankApplicationService.changeStatus(bankApplicationId, BankApplicationStatus.SENDING_TO_BANK, 1234);
        return notificationResponse;
    }

    @Override
    public Optional<NotificationPartnerLetterResponse> getPartnerInfoForPartnerLetter(NotificationPartnerLetterRequest request) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(request.getBorrowerId());
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        List<PartnerContactResponse> partnerContactResponses = getPartnerContactResponses(partnerApplication, request.getTrigger());
        if (partnerContactResponses.isEmpty()) {
            return Optional.empty();
        }
        if (request.getTrigger().equals(NotificationTrigger.BORROWER_SIGNED_FORM) && !statusService.isApplicationFullySigned(borrowerProfile)) {
            return Optional.empty();
        }
        Set<String> bankNames = getBankNames(partnerApplication);
        NotificationPartnerLetterResponse response = new NotificationPartnerLetterResponse()
                .setResidentialComplexName(partnerApplication.getRealEstate().getResidentialComplexName())
                .setAddress(partnerApplication.getRealEstate().getAddress())
                .setRealEstateTypes(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class))
                .setCreditPurposeType(partnerApplication.getCreditPurposeType())
                .setDownPayment(partnerApplication.getMortgageCalculation().getDownPayment())
                .setRealEstatePrice(partnerApplication.getMortgageCalculation().getRealEstatePrice())
                .setCreditSize(partnerApplication.getMortgageCalculation().getRealEstatePrice()
                        .subtract(partnerApplication.getMortgageCalculation().getDownPayment()))
                .setMonthCreditTerm(Optional.ofNullable(partnerApplication.getMortgageCalculation())
                        .map(MortgageCalculation::getMonthCreditTerm).orElse(0))
                .setFirstName(borrowerProfile.getFirstName())
                .setLastName(borrowerProfile.getLastName())
                .setMiddleName(borrowerProfile.getMiddleName())
                .setBankNames(bankNames)
                .setPartnerContacts(partnerContactResponses)
                .setTrigger(request.getTrigger());
        return Optional.of(response);
    }

    private List<PartnerContactResponse> getPartnerContactResponses(PartnerApplication partnerApplication, NotificationTrigger trigger) {
        return partnerApplication.getPartner().getPartnerContacts().stream()
                .filter(contact -> Converter.convertStringListToEnumList(contact.getTriggers(), NotificationTrigger.class).contains(trigger))
                .map(partnerContactMapper::toPartnerContactResponse)
                .collect(Collectors.toList());
    }

    private Set<String> getBankNames(PartnerApplication partnerApplication) {
        return partnerApplication.getBankApplications().stream()
                .map(BankApplication::getCreditProgram)
                .map(CreditProgram::getBank)
                .map(Bank::getName)
                .collect(Collectors.toSet());
    }

    private NotificationBankLetterResponse fetchNotificationData(UUID bankApplicationId) {
        log.info("Fetching information for letter formation");
        NotificationBankLetterResponse response = new NotificationBankLetterResponse();
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        lockBaseRateToBankApplication(bankApplication);
        BorrowerProfile borrowerProfile = bankApplication.getMainBorrower();
        if (borrowerProfile != null) {
            PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
            if (partnerApplication != null) {
                RealEstate realEstate = partnerApplication.getRealEstate();
                MortgageCalculation mortgageCalculation = partnerApplication.getMortgageCalculation();
                Bank bank = bankApplication.getCreditProgram().getBank();
                response.setBankId(bankApplicationId);
                response.setPartnerApplicationId(borrowerProfile.getPartnerApplication().getId());
                response.setApplicationNumber(bankApplication.getApplicationNumber());
                response.setBorrowerId(borrowerProfile.getId());
                response.setPartnerName(partnerApplication.getPartner().getName());
                if (realEstate != null) {
                    response.setRegionType(realEstate.getRegion());
                    response.setResidentialComplexName(realEstate.getResidentialComplexName());
                    response.setAddress(realEstate.getAddress());
                    response.setRealEstateTypeName(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class).stream()
                            .map(RealEstateType::getName)
                            .collect(Collectors.joining(", ")));

                }
                response.setRealEstateTypes(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class));
                response.setCreditPurposeType(partnerApplication.getCreditPurposeType());
                response.setProgramName(bankApplication.getCreditProgram().getProgramName());
                if (mortgageCalculation != null) {
                    response.setRealEstatePrice(mortgageCalculation.getRealEstatePrice());
                    response.setDownPayment(mortgageCalculation.getDownPayment());
                    response.setCreditSize(mortgageCalculation.getRealEstatePrice()
                            .subtract(mortgageCalculation.getDownPayment()));
                }
                response.setMonthCreditTerm(bankApplication.getMonthCreditTerm());
                response.setLastName(borrowerProfile.getLastName());
                response.setFirstName(borrowerProfile.getFirstName());
                response.setMiddleName(borrowerProfile.getMiddleName() == null
                        ? ""
                        : borrowerProfile.getMiddleName());
                if (bank != null) {
                    response.setBankId(bank.getId());
                    response.setBankName(bank.getName());
                }
            } else {
                throw new DataNotFoundException("PartnerApplication is not found");
            }
        } else {
            throw new DataNotFoundException("BorrowerProfile is not found");
        }

        return response;
    }

    private void lockBaseRateToBankApplication(BankApplication bankApplication) {
        BankApplication application = bankApplication
                .setLockBaseRate(bankApplication.getCreditProgram().getBaseRate());
        List<UUID> salaryBanks = bankApplication.getPartnerApplication().getMortgageCalculation().getSalaryBanks().stream().map(Bank::getId).collect(Collectors.toList());
        UUID id = bankApplication.getCreditProgram().getBank().getId();
        if (salaryBanks.contains(id)) {
            double modifyBaseRate = bankApplication.getCreditProgram().getBaseRate()
                    + bankApplication.getCreditProgram().getSalaryClientInterestRate();
            application.setLockSalaryBaseRate(modifyBaseRate);
        }
        bankApplicationService.save(application);
    }

    private void enrichNotificationResponseWithData(NotificationBankLetterResponse response, UUID bankApplicationId) {
        UUID partnerApplicationId = response.getPartnerApplicationId();
        BorrowerResponse borrowers = borrowerProfileService.getBorrowersByPartnerApplicationId(partnerApplicationId);
        response.setAttachmentIds(extractAllAttachmentsIds(borrowers));
        log.info("Fetching email addresses for sending");
        response.setEmails(bankApplicationRepository.getEmailsByBankApplicationId(bankApplicationId));
        response.setCreditPurposeTypeName(response.getCreditPurposeType().getName());
        response.setBorrowerResponse(borrowers);
    }

    private Set<Long> extractAllAttachmentsIds(BorrowerResponse borrowers) {
        Set<Long> mainBorrowerDocIds = getAllAttachmentsIds(borrowers.getMainBorrower().getDocuments());
        Set<Long> ids = new HashSet<>(mainBorrowerDocIds);
        borrowers.getCoBorrower().forEach(d -> ids.addAll(getAllAttachmentsIds(d.getDocuments())));
        return ids;
    }

    private Set<Long> getAllAttachmentsIds(List<BorrowerDocumentResponse> documents) {
        return documents.stream()
                .filter(d -> d.getDocumentType() != DocumentType.GENERATED_FORM)
                .map(BorrowerDocumentResponse::getAttachmentId).collect(Collectors.toSet());
    }
}
