package pro.mbroker.app.service;

import pro.mbroker.api.dto.LoanProgramCalculationDto;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface CalculatorService {

    PropertyMortgageDTO getCreditOffer(CalculatorRequest request);

    BigDecimal calculateMonthlyPayment(BigDecimal mortgageSum, double annualInterestRate, int loanTermInMonths);

    BigDecimal calculateOverpayment(BigDecimal monthlyPayment, int loanTermMonths, BigDecimal realEstatePrice, BigDecimal downPayment);

    BigDecimal getMortgageSum(BigDecimal realEstatePrice, BigDecimal downPayment);

    LoanProgramCalculationDto getCreditOfferByCreditProgramId(UUID creditProgramId, CalculatorRequest request);
}
