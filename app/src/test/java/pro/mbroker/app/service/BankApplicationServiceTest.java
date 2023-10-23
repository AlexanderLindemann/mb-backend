package pro.mbroker.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.BankApplication;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BankApplicationServiceTest extends AbstractServiceTest {
    @Autowired
    private BankApplicationService bankApplicationService;

    @Autowired
    private TestData testData;


    @Test
    public void testGetBankApplication() {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        assertEquals(bankApplication.getCreditProgram().getId(), UUID.fromString("bfda8d66-d926-11ed-afa1-0242ac120002"));
        assertEquals(bankApplication.getMainBorrower().getId(), UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.DATA_NO_ENTERED);
        assertEquals(bankApplication.getMonthlyPayment(), BigDecimal.valueOf(60000));
        assertEquals(bankApplication.getRealEstatePrice(), BigDecimal.valueOf(10000000));
        assertEquals(bankApplication.getDownPayment(), BigDecimal.valueOf(500000));
        assertEquals(bankApplication.getMonthCreditTerm(), 120);
        assertEquals(bankApplication.getOverpayment(), BigDecimal.valueOf(6000000));
        assertEquals(bankApplication.getPartnerApplication().getId(), UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        assertEquals(bankApplication.getId(), UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        assertEquals(bankApplication.isActive(), true);
    }

    @Test
    public void testUpdateBankApplication() {
        BankApplication bankApplication = bankApplicationService.updateBankApplication(testData.getBankApplicationRequest());
        assertEquals(bankApplication.getCreditProgram().getId(), UUID.fromString("bfda8d66-d926-11ed-afa1-0242ac120002"));
        assertEquals(bankApplication.getMainBorrower().getId(), UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.DATA_NO_ENTERED);
        assertEquals(bankApplication.getMonthlyPayment(), BigDecimal.valueOf(100000));
        assertEquals(bankApplication.getRealEstatePrice(), BigDecimal.valueOf(1000000000));
        assertEquals(bankApplication.getDownPayment(), BigDecimal.valueOf(1000000));
        assertEquals(bankApplication.getMonthCreditTerm(), 360);
        assertEquals(bankApplication.getOverpayment(), BigDecimal.valueOf(7000000));
    }

    @Test
    public void testChangeMainBorrowerByBankApplicationId() {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        assertEquals(bankApplication.getMainBorrower().getId(), UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        bankApplicationService.changeMainBorrowerByBankApplicationId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"), UUID.fromString("7cb535d6-f92e-11ed-be56-0242ac120002"));
        assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.DATA_NO_ENTERED);
        assertEquals(bankApplication.getMonthlyPayment(), BigDecimal.valueOf(60000));
        assertEquals(bankApplication.getRealEstatePrice(), BigDecimal.valueOf(10000000));
        assertEquals(bankApplication.getDownPayment(), BigDecimal.valueOf(500000));
        assertEquals(bankApplication.getMonthCreditTerm(), 120);
        assertEquals(bankApplication.getOverpayment(), BigDecimal.valueOf(6000000));
        assertEquals(bankApplication.getPartnerApplication().getId(), UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        assertEquals(bankApplication.getId(), UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        assertEquals(bankApplication.isActive(), true);
    }

    @Test
    public void testChangeMainBorrowerByBankApplicationId2() {
        BankApplication bankApplication = bankApplicationService.changeStatus(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"), BankApplicationStatus.EXPIRED);
        assertEquals(bankApplication.getBankApplicationStatus(), BankApplicationStatus.EXPIRED);
        assertEquals(bankApplication.getMonthlyPayment(), BigDecimal.valueOf(60000));
        assertEquals(bankApplication.getRealEstatePrice(), BigDecimal.valueOf(10000000));
        assertEquals(bankApplication.getDownPayment(), BigDecimal.valueOf(500000));
        assertEquals(bankApplication.getMonthCreditTerm(), 120);
        assertEquals(bankApplication.getOverpayment(), BigDecimal.valueOf(6000000));
        assertEquals(bankApplication.getPartnerApplication().getId(), UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        assertEquals(bankApplication.getId(), UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        assertTrue(bankApplication.isActive());
    }

}
