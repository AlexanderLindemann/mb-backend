package pro.mbroker.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankContactMapper;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.model.bank.Bank;
import pro.mbroker.app.model.bank.BankContact;
import pro.mbroker.app.model.bank.BankContactRepository;
import pro.mbroker.app.model.bank.BankRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankContactRepository bankContactRepository;
    private final BankMapper bankMapper;
    private final BankContactMapper bankContactMapper;

    public BankServiceImpl(BankRepository bankRepository, BankContactRepository bankContactRepository, BankMapper bankMapper, BankContactMapper bankContactMapper) {
        this.bankRepository = bankRepository;
        this.bankContactRepository = bankContactRepository;
        this.bankMapper = bankMapper;
        this.bankContactMapper = bankContactMapper;
    }

    @Override
    public BankResponse createBank(String name, MultipartFile logoFile) {
        Bank bank = new Bank();
        bank.setName(name);
        try {
            byte[] logoBytes = logoFile.getBytes();
            bank.setLogo(Base64.getEncoder().encodeToString(logoBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bank savedBank = bankRepository.save(bank);
        return bankMapper.toBankResponseMapper(savedBank);
    }

    @Override
    public BankResponse getBankById(UUID id) {
        Bank bank = getBank(id);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public BankResponse addBankContact(UUID id, BankContactRequest contactRequest) {
        Bank bank = getBank(id);
        List<BankContact> contacts = bank.getContacts();
        contacts.add(bankContactMapper.toBankContactMapper(contactRequest));
        bank.setContacts(contacts);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public void deleteBankById(UUID id) {
        bankRepository.deleteById(id);
    }

    @Override
    public BankResponse deleteBankContact(UUID contactId) {
        BankContact bankContact = bankContactRepository.findById(contactId)
                .orElseThrow(() -> new ItemNotFoundException(BankContact.class, contactId));
        UUID bankId = bankContact.getBank().getId();
        bankContactRepository.deleteById(contactId);
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
        return bankMapper.toBankResponseMapper(bank);
    }

    private Bank getBank(UUID id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, id));
    }
}
