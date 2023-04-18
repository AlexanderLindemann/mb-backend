package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.UUID;

@Api(value = "API Калькулятора", tags = "API Калькулятора")
@RestController
@RequestMapping("/public/calculator")
public interface CalculatorController {

    @ApiOperation("Получить предложения от банков")
    @GetMapping()
    PropertyMortgageDTO getCreditOffer(@RequestParam UUID realEstateId,
                                       @RequestParam CreditPurposeType creditPurposeType,
                                       @RequestParam RealEstateType realEstateType,
                                       @RequestParam(required = false) BigDecimal realEstatePrice,
                                       @RequestParam(required = false) BigDecimal downPayment,
                                       @RequestParam(required = false) Integer maxMonthlyPayment,
                                       @RequestParam(required = false) Integer creditTerm,
                                       @RequestParam(required = false) Boolean isMaternalCapital);
}
