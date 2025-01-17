package pro.mbroker.app.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.LoanProgramCalculationDto;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.app.TestData;
import pro.mbroker.app.service.impl.CalculatorServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorServiceTest extends BaseServiceTest {
    @Autowired
    private CalculatorServiceImpl calculatorService;
    @Autowired
    private TestData testData;

    @Test
    public void testCalculateMonthlyPayment() {
        BigDecimal loanAmount = new BigDecimal("500000");
        BigDecimal downPayment = new BigDecimal("100000");
        double annualInterestRate = 5.0;
        int loanTermInMonths = 360;
        BigDecimal expectedMonthlyPayment = new BigDecimal("2147.29");
        BigDecimal actualMonthlyPayment = calculatorService.calculateMonthlyPayment(loanAmount.subtract(downPayment), annualInterestRate, loanTermInMonths);
        assertEquals(expectedMonthlyPayment, actualMonthlyPayment);
    }

    @Test
    public void testCalculateOverpayment() {
        BigDecimal monthlyPayment = new BigDecimal("2147.29");
        int loanTermMonths = 360;
        BigDecimal realEstatePrice = new BigDecimal("500000");
        BigDecimal downPayment = new BigDecimal("100000");
        BigDecimal expectedOverpayment = new BigDecimal("373024.40");
        BigDecimal actualOverpayment = calculatorService.calculateOverpayment(monthlyPayment, loanTermMonths, realEstatePrice, downPayment);
        assertEquals(expectedOverpayment, actualOverpayment);
    }

    @Test
    public void testGetCreditOffer() {
        PropertyMortgageDTO creditOffer = calculatorService.getCreditOffer(testData.getCalculatorRequest());
        assertEquals(creditOffer.getLoanProgramCalculationDto().get(0).getMonthlyPayment(), new BigDecimal("237899.30"));
        assertEquals(creditOffer.getLoanProgramCalculationDto().get(0).getOverpayment(), new BigDecimal("4273958.00"));
    }

    @Test
    public void testGetCreditOfferWithSalaryBankClient() {
        PropertyMortgageDTO creditOffer = calculatorService.getCreditOffer(testData.getCalculatorRequest().setSalaryBanks(List.of(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"))));
        List<LoanProgramCalculationDto> loanProgramCalculationDto = creditOffer.getLoanProgramCalculationDto();
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getCalculatedRate(), 13.0);
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getSalaryBankRate(), -2.0);
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getMonthlyPayment(), new BigDecimal("227530.70"));
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getOverpayment(), new BigDecimal("3651842.00"));
    }

    @Test
    public void testCreditOfferCount() {
        Integer count = calculatorService.getCreditOfferCount(testData.getCalculatorRequest());
        assertEquals(count, 2);
    }

    @Test
    public void testGetCreditOfferWithBigMonthlyPayment() {
        PropertyMortgageDTO creditOffer = calculatorService.getCreditOffer(testData.getCalculatorRequest().setCreditTerm(null).setMaxMonthlyPayment(BigDecimal.valueOf(500000)));
        List<LoanProgramCalculationDto> loanProgramCalculationDto = creditOffer.getLoanProgramCalculationDto();
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getCalculatedRate(), 13.0);
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getSalaryBankRate(), -2.0);
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getMonthlyPayment(), new BigDecimal("336939.50"));
        assertEquals(loanProgramCalculationDto.get(0).getSalaryClientCalculation().getOverpayment(), new BigDecimal("2129822.00"));
    }
}
