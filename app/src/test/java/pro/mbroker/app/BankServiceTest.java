package pro.mbroker.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.service.BankService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BankServiceTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private BankRepository bankRepository;

    @Test
    public void testCreateBank() {
        // Создаем новый банк
        String name = "Test Bank";
        Bank bank = bankService.createBank(name);

        // Проверяем, что банк был успешно создан
        assertNotNull(bank.getId());
        assertEquals(name, bank.getName());
        assertTrue(bank.getOrderNumber() > 0);

        // Проверяем, что банк был сохранен в БД
        Bank savedBank = bankRepository.findById(bank.getId()).orElse(null);
        assertNotNull(savedBank);
        assertEquals(bank.getId(), savedBank.getId());
        assertEquals(name, savedBank.getName());
        assertEquals(bank.getOrderNumber(), savedBank.getOrderNumber());
    }

}
