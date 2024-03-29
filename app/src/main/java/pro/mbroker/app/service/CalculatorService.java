package pro.mbroker.app.service;

import pro.mbroker.api.dto.LoanProgramCalculationDto;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.SalaryClientProgramCalculationDto;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.app.entity.BankApplication;

import java.math.BigDecimal;
import java.util.UUID;

public interface CalculatorService {

    PropertyMortgageDTO getCreditOffer(CalculatorRequest request);

    BigDecimal calculateMonthlyPayment(BigDecimal mortgageSum, double annualInterestRate, int loanTermInMonths);

    BigDecimal calculateOverpayment(BigDecimal monthlyPayment, int loanTermMonths, BigDecimal realEstatePrice, BigDecimal downPayment);

    BigDecimal calculateMortgageSum(BigDecimal realEstatePrice, BigDecimal downPayment);

    LoanProgramCalculationDto getCreditOfferByCreditProgramId(UUID creditProgramId, CalculatorRequest request);

    Integer getCreditOfferCount(CalculatorRequest calculatorRequest);

    SalaryClientProgramCalculationDto getSalaryClientProgramCalculationDto(BankApplication bankApplication);
}
