package pro.mbroker.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.service.impl.CalculatorServiceImpl;

import java.math.BigDecimal;
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


    @Test
    public void testCalculateMonthlyPayment() {
        BigDecimal loanAmount = new BigDecimal("500000");
        BigDecimal downPayment = new BigDecimal("100000");
        double annualInterestRate = 5.0;
        int loanTermInMonths = 360;

        BigDecimal expectedMonthlyPayment = new BigDecimal("2147.29");
        BigDecimal actualMonthlyPayment = calculatorService.calculateMonthlyPayment(loanAmount, downPayment, annualInterestRate, loanTermInMonths);

        assertEquals(expectedMonthlyPayment, actualMonthlyPayment);
    }

    @Test
    public void testCalculateOverpayment() {
        BigDecimal monthlyPayment = new BigDecimal("2147.29");
        int loanTermMonths = 360;
        BigDecimal totalLoanAmount = new BigDecimal("400000");

        BigDecimal expectedOverpayment = new BigDecimal("373024.40");
        BigDecimal actualOverpayment = calculatorService.calculateOverpayment(monthlyPayment, loanTermMonths, totalLoanAmount);

        assertEquals(expectedOverpayment, actualOverpayment);
    }

    @Test
    public void testGetCreditOffer() {

        CalculatorRequest calculatorRequest = new CalculatorRequest();
        calculatorRequest.setRealEstateId(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"));
        calculatorRequest.setCreditTerm(5);
        calculatorRequest.setCreditPurposeType(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION);
        calculatorRequest.setDownPayment(BigDecimal.valueOf(1000000));
        calculatorRequest.setMortgageSum(BigDecimal.valueOf(10000000));
        calculatorRequest.setRealEstateType(RealEstateType.APARTMENT);
        calculatorRequest.setIsMaternalCapital(true);

        PropertyMortgageDTO creditOffer = calculatorService.getCreditOffer(calculatorRequest);
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(0).getMonthlyPayment(), new BigDecimal("214109.37"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(0).getOverpayment(), new BigDecimal("2846562.20"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(1).getMonthlyPayment(), new BigDecimal("238444.97"));
        assertEquals(creditOffer.getBankLoanProgramDto().get(0).getLoanProgramCalculationDto().get(1).getOverpayment(), new BigDecimal("4306698.20"));
    }

}
