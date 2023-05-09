package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.repository.BankRepository;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.yaml")
public class BankServiceTest {

    @Autowired
    private BankService bankService;
    @Autowired
    private BankRepository bankRepository;
    @MockBean
    private CurrentUserService currentUserService;

    @Value("${test_token}")
    private String apiToken;

    private static final UUID BANK_ID = UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002");

    @Before
    public void setUp() {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(apiToken);
    }

    @Test
    public void testGetAllBankWithPagination() {
        List<Bank> allBankSortByName = bankService.getAllBank(0, 2, "name", "asc");
        assertThat(allBankSortByName.size(), Matchers.is(2));
        assertThat(allBankSortByName.get(0).getName(), Matchers.is("aTestBank1"));
        assertThat(allBankSortByName.get(1).getName(), Matchers.is("bTestBank2"));
        List<Bank> allBankSortByOrderNumber = bankService.getAllBank(0, 3, "orderNumber", "asc");
        assertThat(allBankSortByOrderNumber.size(), Matchers.is(3));
        assertThat(allBankSortByOrderNumber.get(0).getName(), Matchers.is("dTestBank4"));
        assertThat(allBankSortByOrderNumber.get(2).getName(), Matchers.is("bTestBank2"));
    }

    @Test
    public void testCreateBank() {
        String name = "Test Bank";
        Bank bank = bankService.createBank(name);
        assertNotNull(bank.getId());
        assertEquals(name, bank.getName());
        assertTrue(bank.getOrderNumber() > 0);
        Bank savedBank = bankRepository.findById(bank.getId()).orElse(null);
        assertNotNull(savedBank);
        assertEquals(bank.getId(), savedBank.getId());
        assertEquals(name, savedBank.getName());
        assertEquals(bank.getOrderNumber(), savedBank.getOrderNumber());
    }

    @Test
    public void testGetBankById() {
        Bank bank = bankService.getBankById(BANK_ID);
        assertNotNull(bank.getId());
        assertEquals("aTestBank1", bank.getName());
    }

    @Test
    public void testDeleteBank() {
        bankService.deleteBankById(BANK_ID);
        List<Bank> allBankSortByName = bankService.getAllBank(0, 10, "name", "asc");
        assertThat(allBankSortByName.size(), Matchers.is(3));
        List<UUID> bankIds = allBankSortByName.stream().map(Bank::getId).collect(Collectors.toList());
        assertFalse(bankIds.contains(BANK_ID));
    }

}
