package pro.mbroker.app.service;

import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;

import java.util.List;
import java.util.UUID;

public interface BankContactService {

    Bank addBankContact(UUID id, String fullName, String email, Integer sdId);

    void deleteBankContact(UUID contactId, Integer sdId);

    List<BankContact> getBankContact(UUID bankId);
}
