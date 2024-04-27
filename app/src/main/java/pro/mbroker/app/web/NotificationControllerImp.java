package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.NotificationController;
import pro.mbroker.api.dto.request.notification.NotificationPartnerLetterRequest;
import pro.mbroker.api.dto.response.notification.NotificationBankLetterResponse;
import pro.mbroker.api.dto.response.notification.NotificationPartnerLetterResponse;
import pro.mbroker.api.dto.response.PartnerContactResponse;
import pro.mbroker.api.enums.NotificationTrigger;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.MortgageCalculation;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.mapper.PartnerContactMapper;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.NotificationService;
import pro.mbroker.app.service.StatusService;
import pro.mbroker.app.util.Converter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationControllerImp implements NotificationController {

    private final NotificationService notificationService;
    private final BorrowerProfileService borrowerProfileService;
    private final StatusService statusService;
    private final PartnerContactMapper partnerContactMapper;

    @Override
    public NotificationBankLetterResponse getCustomerInfoForBankLetter(UUID bankApplicationId) {
        return notificationService.getCustomerInfoForBankLetter(bankApplicationId);
    }

    @Override
    public Optional<NotificationPartnerLetterResponse> isApplicationFullySigned(NotificationPartnerLetterRequest request) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(request.getBorrowerId());
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        List<PartnerContactResponse> partnerContactResponses = partnerApplication.getPartner().getPartnerContacts().stream()
                .filter(partnerContact -> Converter.convertStringListToEnumList(partnerContact.getTriggers(), NotificationTrigger.class)
                        .contains(request.getTrigger()))
                .map(partnerContactMapper::toPartnerContactResponse)
                .collect(Collectors.toList());
        boolean applicationFullySigned = statusService.isApplicationFullySigned(borrowerProfile);
        NotificationPartnerLetterResponse notificationBankLetterResponse;
        if (applicationFullySigned && Objects.nonNull(partnerContactResponses)) {
            Set<String> bankNames = partnerApplication.getBankApplications().stream()
                    .map(BankApplication::getCreditProgram)
                    .map(CreditProgram::getBank)
                    .map(Bank::getName)
                    .collect(Collectors.toSet());
            notificationBankLetterResponse = new NotificationPartnerLetterResponse()
                    .setResidentialComplexName(partnerApplication.getRealEstate().getResidentialComplexName())
                    .setAddress(partnerApplication.getRealEstate().getAddress())
                    .setRealEstateTypes(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class))
                    .setCreditPurposeType(partnerApplication.getCreditPurposeType())
                    .setDownPayment(partnerApplication.getMortgageCalculation().getDownPayment())
                    .setRealEstatePrice(partnerApplication.getMortgageCalculation().getRealEstatePrice())
                    .setCreditSize(partnerApplication.getMortgageCalculation().getRealEstatePrice()
                            .subtract(partnerApplication.getMortgageCalculation().getDownPayment()));
            notificationBankLetterResponse.setMonthCreditTerm(Optional.ofNullable(partnerApplication.getMortgageCalculation())
                    .map(MortgageCalculation::getMonthCreditTerm).orElse(0));
            notificationBankLetterResponse.setFirstName(borrowerProfile.getFirstName());
            notificationBankLetterResponse.setLastName(borrowerProfile.getLastName());
            notificationBankLetterResponse.setMiddleName(borrowerProfile.getMiddleName());
            notificationBankLetterResponse.setBankNames(bankNames);
            notificationBankLetterResponse.setPartnerContacts(partnerContactResponses);
            return Optional.of(notificationBankLetterResponse);
        }
        return Optional.empty();
    }
}
