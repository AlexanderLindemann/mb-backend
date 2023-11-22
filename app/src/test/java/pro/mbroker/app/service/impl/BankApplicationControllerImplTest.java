package pro.mbroker.app.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.mbroker.app.entity.underwriting.Underwriting;
import pro.mbroker.app.entity.underwriting.UnderwritingDecision;
import pro.mbroker.app.entity.underwriting.UnderwritingError;
import pro.mbroker.app.service.AbstractServiceTest;
import pro.mbroker.app.util.UnderwritingComparator;

@SpringBootTest
class BankApplicationControllerImplTest extends AbstractServiceTest {

    @Test
    void testUnderwritingIsChanged() {
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
    void testUnderwritingIsChanged_with_error() {
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
    void testUnderwritingIsChanged_with_old_decision_and_new_decision() {
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