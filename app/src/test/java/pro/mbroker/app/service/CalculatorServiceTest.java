package pro.mbroker.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.app.TestData;
import pro.mbroker.app.service.impl.CalculatorServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CalculatorServiceTest {
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
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(0).getMonthlyPayment(), new BigDecimal("237899.30"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(0).getOverpayment(), new BigDecimal("4273958.00"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(1).getMonthlyPayment(), new BigDecimal("264938.90"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(1).getOverpayment(), new BigDecimal("5896334.00"));
    }

    @Test
    public void testGetCreditOfferWithSalaryBankClient() {

        PropertyMortgageDTO creditOffer = calculatorService.getCreditOffer(testData.getCalculatorRequest().setSalaryBanks(List.of(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"))));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(0).getMonthlyPayment(), new BigDecimal("227530.70"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(0).getOverpayment(), new BigDecimal("3651842.00"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(1).getMonthlyPayment(), new BigDecimal("248525.80"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(1).getOverpayment(), new BigDecimal("4911548.00"));
    }

}
