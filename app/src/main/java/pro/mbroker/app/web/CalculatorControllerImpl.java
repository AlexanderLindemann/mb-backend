package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CalculatorController;
import pro.mbroker.api.dto.LoanProgramCalculationDto;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.api.dto.response.CreditOfferCountResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.service.CalculatorService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculatorControllerImpl implements CalculatorController {
    private final CalculatorService calculatorService;

    @Override
    public PropertyMortgageDTO getCreditOffer(String realEstateId,
                                              CreditPurposeType creditPurposeType,
                                              List<RealEstateType> realEstateTypes,
                                              BigDecimal realEstatePrice,
                                              BigDecimal downPayment,
                                              BigDecimal maxMonthlyPayment,
                                              UUID partnerApplicationId,
                                              Integer creditTerm,
                                              Boolean isMaternalCapital,
                                              List<UUID> salaryBanks) {
        return calculatorService.getCreditOffer(new CalculatorRequest()
                .setRealEstateId(realEstateId)
                .setCreditPurposeType(creditPurposeType)
                .setRealEstateTypes(realEstateTypes)
                .setRealEstatePrice(realEstatePrice)
                .setDownPayment(downPayment)
                .setPartnerApplicationId(partnerApplicationId)
                .setMaxMonthlyPayment(maxMonthlyPayment)
                .setCreditTerm(creditTerm)
                .setIsMaternalCapital(isMaternalCapital)
                .setSalaryBanks(salaryBanks));
    }

    @Override
    public CreditOfferCountResponse getCreditOfferCount(String realEstateId,
                                                        CreditPurposeType creditPurposeType,
                                                        List<RealEstateType> realEstateTypes,
                                                        BigDecimal realEstatePrice,
                                                        BigDecimal downPayment,
                                                        BigDecimal maxMonthlyPayment,
                                                        UUID partnerApplicationId,
                                                        Integer creditTerm,
                                                        Boolean isMaternalCapital) {
        return new CreditOfferCountResponse().setCount(calculatorService.getCreditOfferCount(new CalculatorRequest()
                .setRealEstateId(realEstateId)
                .setCreditPurposeType(creditPurposeType)
                .setRealEstateTypes(realEstateTypes)
                .setRealEstatePrice(realEstatePrice)
                .setDownPayment(downPayment)
                .setPartnerApplicationId(partnerApplicationId)
                .setMaxMonthlyPayment(maxMonthlyPayment)
                .setCreditTerm(creditTerm)
                .setIsMaternalCapital(isMaternalCapital)));
    }

    @Override
    public LoanProgramCalculationDto getCreditOfferByCreditProgramId(UUID creditProgramId,
                                                                     String realEstateId,
                                                                     CreditPurposeType creditPurposeType,
                                                                     List<RealEstateType> realEstateTypes,
                                                                     BigDecimal realEstatePrice,
                                                                     BigDecimal downPayment,
                                                                     BigDecimal maxMonthlyPayment,
                                                                     Integer creditTerm,
                                                                     Boolean isMaternalCapital) {
        return calculatorService.getCreditOfferByCreditProgramId(creditProgramId, new CalculatorRequest()
                .setRealEstateId(realEstateId)
                .setCreditPurposeType(creditPurposeType)
                .setRealEstateTypes(realEstateTypes)
                .setRealEstatePrice(realEstatePrice)
                .setDownPayment(downPayment)
                .setMaxMonthlyPayment(maxMonthlyPayment)
                .setCreditTerm(creditTerm)
                .setIsMaternalCapital(isMaternalCapital));
    }
}
