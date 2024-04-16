package pro.mbroker.app.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.repository.BankRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static pro.mbroker.app.TestConstants.BANK_ID1;


public class BankServiceTest extends BaseServiceTest {
    @Autowired
    private BankService bankService;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private TestData testData;

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
        Bank bank = bankService.createBank(testData.getBankRequest(), 1234);
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
        Bank bank = bankService.getBankById(BANK_ID1);
        assertNotNull(bank.getId());
        assertEquals("aTestBank1", bank.getName());
    }

    @Test
    public void testDeleteBank() {
        bankService.deleteBankById(BANK_ID1, 1234);
        List<Bank> allBankSortByName = bankService.getAllBank(0, 10, "name", "asc");
        assertThat(allBankSortByName.size(), Matchers.is(3));
        List<UUID> bankIds = allBankSortByName.stream().map(Bank::getId).collect(Collectors.toList());
        assertFalse(bankIds.contains(BANK_ID1));
    }

}
