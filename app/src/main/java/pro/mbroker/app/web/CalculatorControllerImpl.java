package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CalculatorController;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.service.CalculatorService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculatorControllerImpl implements CalculatorController {
    private final CalculatorService calculatorService;

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN)")
    public PropertyMortgageDTO getCreditOffer(UUID realEstateId,
                                              CreditPurposeType creditPurposeType,
                                              RealEstateType realEstateType,
                                              BigDecimal realEstatePrice,
                                              BigDecimal downPayment,
                                              Integer maxMonthlyPayment,
                                              Integer creditTerm,
                                              Boolean isMaternalCapital) {

        return calculatorService.getCreditOffer(new CalculatorRequest()
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