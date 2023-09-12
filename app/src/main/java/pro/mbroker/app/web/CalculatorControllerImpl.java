package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CalculatorController;
import pro.mbroker.api.dto.LoanProgramCalculationDto;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;
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
    public PropertyMortgageDTO getCreditOffer(UUID realEstateId,
                                              CreditPurposeType creditPurposeType,
                                              RealEstateType realEstateType,
                                              BigDecimal realEstatePrice,
                                              BigDecimal downPayment,
                                              BigDecimal maxMonthlyPayment,
                                              Integer creditTerm,
                                              Boolean isMaternalCapital,
                                              List<UUID> salaryBanks) {
        return calculatorService.getCreditOffer(new CalculatorRequest()
                .setRealEstateId(realEstateId)
                .setCreditPurposeType(creditPurposeType)
                .setRealEstateType(realEstateType)
                .setRealEstatePrice(realEstatePrice)
                .setDownPayment(downPayment)
                .setMaxMonthlyPayment(maxMonthlyPayment)
                .setCreditTerm(creditTerm)
                .setIsMaternalCapital(isMaternalCapital)
                .setSalaryBanks(salaryBanks));
    }

    @Override
    public LoanProgramCalculationDto getCreditOfferByCreditProgramId(UUID creditProgramId,
                                                                     UUID realEstateId,
                                                                     CreditPurposeType creditPurposeType,
                                                                     RealEstateType realEstateType,
                                                                     BigDecimal realEstatePrice,
                                                                     BigDecimal downPayment,
                                                                     BigDecimal maxMonthlyPayment,
                                                                     Integer creditTerm,
                                                                     Boolean isMaternalCapital) {
        return calculatorService.getCreditOfferByCreditProgramId(creditProgramId, new CalculatorRequest()
                .setRealEstateId(realEstateId)
                .setCreditPurposeType(creditPurposeType)
                .setRealEstateType(realEstateType)
                .setRealEstatePrice(realEstatePrice)
                .setDownPayment(downPayment)
                .setMaxMonthlyPayment(maxMonthlyPayment)
                .setCreditTerm(creditTerm)
                .setIsMaternalCapital(isMaternalCapital));
    }

}
