package pro.mbroker.app.service;

import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;

import java.util.List;
import java.util.UUID;

public interface BankContactService {

    Bank addBankContact(UUID id, String fullName, String email);

    Bank deleteBankContact(UUID contactId);

    List<BankContact> getBankContact(UUID bankId);
}
