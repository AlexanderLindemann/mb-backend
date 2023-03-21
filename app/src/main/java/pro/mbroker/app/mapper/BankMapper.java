package pro.mbroker.app.mapper;

import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.model.bank.Bank;

public interface BankMapper {
    BankResponse toBankResponseMapper(Bank bank);

}