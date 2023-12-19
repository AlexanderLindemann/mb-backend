package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) " +
            "or hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN)" +
            "or hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public PropertyMortgageDTO getCreditOffer(UUID realEstateId,
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
    public Integer getCreditOfferCount(UUID realEstateId, CreditPurposeType creditPurposeType, List<RealEstateType> realEstateTypes, BigDecimal realEstatePrice, BigDecimal downPayment, BigDecimal maxMonthlyPayment, UUID partnerApplicationId, Integer creditTerm, Boolean isMaternalCapital) {
        return calculatorService.getCreditOfferCount(new CalculatorRequest()
                .setRealEstateId(realEstateId)
                .setCreditPurposeType(creditPurposeType)
                .setRealEstateTypes(realEstateTypes)
                .setRealEstatePrice(realEstatePrice)
                .setDownPayment(downPayment)
                .setPartnerApplicationId(partnerApplicationId)
                .setMaxMonthlyPayment(maxMonthlyPayment)
                .setCreditTerm(creditTerm)
                .setIsMaternalCapital(isMaternalCapital));
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) " +
            "or hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN)" +
            "or hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public LoanProgramCalculationDto getCreditOfferByCreditProgramId(UUID creditProgramId,
                                                                     UUID realEstateId,
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
