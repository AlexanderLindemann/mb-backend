package pro.mbroker.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.underwriting.Underwriting;
import pro.mbroker.app.entity.underwriting.UnderwritingDecision;
import pro.mbroker.app.entity.underwriting.UnderwritingError;
import pro.mbroker.app.util.UnderwritingComparator;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BankApplicationServiceTest extends BaseServiceTest {
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
        BankApplication bankApplication = bankApplicationService.updateBankApplication(testData.getBankApplicationRequest(), 1234);
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
        bankApplicationService.changeMainBorrowerByBankApplicationId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"), UUID.fromString("7cb535d6-f92e-11ed-be56-0242ac120002"), 1234);
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
        BankApplication bankApplication = bankApplicationService.changeStatus(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"), BankApplicationStatus.EXPIRED, 1234);
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

    @Test
    public void testUnderwritingIsChanged() {
        Underwriting oldUnderwriting = new Underwriting();
        Underwriting updatedUnderwriting = new Underwriting();

        Assertions.assertFalse(UnderwritingComparator.underwritingIsChanged(oldUnderwriting, updatedUnderwriting));

        UnderwritingDecision decision = new UnderwritingDecision();
        decision.setDescription("Одобрен");
        decision.setStatus(3);
        updatedUnderwriting.setAdditionalConditionsStep("New Step");
        updatedUnderwriting.setUnderwritingDecision(decision);
        Assertions.assertTrue(UnderwritingComparator.underwritingIsChanged(oldUnderwriting, updatedUnderwriting));
    }

    @Test
    public void testUnderwritingIsChanged_with_error() {
        Underwriting oldUnderwriting = new Underwriting();
        Underwriting updatedUnderwriting = new Underwriting();

        UnderwritingDecision decision = new UnderwritingDecision();
        decision.setDescription("Отказ");
        decision.setStatus(2);

        oldUnderwriting.setUnderwritingDecision(decision);

        UnderwritingError error = new UnderwritingError();
        error.setMessage("Низкий доход");
        updatedUnderwriting.setUnderwritingDecision(decision);
        updatedUnderwriting.setUnderwritingError(error);

        Assertions.assertTrue(UnderwritingComparator.underwritingIsChanged(oldUnderwriting, updatedUnderwriting));
    }

    @Test
    public void testUnderwritingIsChanged_with_old_decision_and_new_decision() {
        Underwriting oldUnderwriting = new Underwriting();
        Underwriting updatedUnderwriting = new Underwriting();

        UnderwritingDecision decisionOld = new UnderwritingDecision();
        decisionOld.setDescription("В работе");
        decisionOld.setStatus(1);
        updatedUnderwriting.setUnderwritingDecision(decisionOld);

        UnderwritingDecision decisionNew = new UnderwritingDecision();
        decisionNew.setDescription("Одобрено");
        decisionNew.setStatus(3);
        updatedUnderwriting.setUnderwritingDecision(decisionNew);

        Assertions.assertTrue(UnderwritingComparator.underwritingIsChanged(oldUnderwriting, updatedUnderwriting));
    }

}
