package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.model.bank.Bank;
import pro.mbroker.app.model.bank.BankContact;
import pro.mbroker.app.model.bank.BankContactRepository;
import pro.mbroker.app.model.bank.BankRepository;
import pro.mbroker.app.service.BankContactService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankContactServiceImpl implements BankContactService {

    private final BankRepository bankRepository;
    private final BankContactRepository bankContactRepository;


    @Override
    @Transactional
    public Bank addBankContact(UUID id, String fullName, String email) {
        Bank bank = getBank(id);
        List<BankContact> contacts = bank.getContacts();
        BankContact bankContact = bankContactRepository.save(new BankContact()
                .setBank(bank)
                .setFullName(fullName)
                .setEmail(email));
        contacts.add(bankContact);
        bank.setContacts(contacts);
        return bankRepository.save(bank);
    }


    @Override
    public Bank deleteBankContact(UUID contactId) {
        BankContact bankContact = bankContactRepository.findById(contactId)
                .orElseThrow(() -> new ItemNotFoundException(BankContact.class, contactId));
        UUID bankId = bankContact.getBank().getId();
        bankContactRepository.deleteById(contactId);
        return getBank(bankId);
    }

    private Bank getBank(UUID bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }


}
