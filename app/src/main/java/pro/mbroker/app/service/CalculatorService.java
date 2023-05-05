package pro.mbroker.app.service;

import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;

import java.math.BigDecimal;

public interface CalculatorService {
    PropertyMortgageDTO getCreditOffer(CalculatorRequest request);

    BigDecimal calculateMonthlyPayment(BigDecimal mortgageSum, double annualInterestRate, int loanTermInMonths);

    BigDecimal calculateOverpayment(BigDecimal monthlyPayment, int loanTermMonths, BigDecimal realEstatePrice, BigDecimal downPayment);
}