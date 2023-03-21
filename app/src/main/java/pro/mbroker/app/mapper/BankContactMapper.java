package pro.mbroker.app.mapper;

import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.app.model.bank.BankContact;

public interface BankContactMapper {
    BankContact toBankContactMapper(BankContactRequest bankContactRequest);

}