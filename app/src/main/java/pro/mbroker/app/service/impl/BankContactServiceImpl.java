package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.BankContactRepository;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.specification.BankContactSpecification;
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
        bankContactRepository.save(new BankContact()
                .setBank(bank)
                .setFullName(fullName)
                .setEmail(email));
        bankContactRepository.flush();
        return getBank(id);
    }


    @Override
    @Transactional
    public void deleteBankContact(UUID contactId) {
        BankContact bankContact = bankContactRepository.findById(contactId)
                .orElseThrow(() -> new ItemNotFoundException(BankContact.class, contactId));
        bankContact.setActive(false);
        bankContactRepository.save(bankContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankContact> getBankContact(UUID bankId) {
        Specification<BankContact> specification = BankContactSpecification.bankContactByBankIdAndIsActive(bankId);
        return bankContactRepository.findAll(specification);
    }

    private Bank getBank(UUID bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }

}
