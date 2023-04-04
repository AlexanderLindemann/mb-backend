package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.app.model.bank.Bank;

@Mapper
public interface BankMapper {
    BankResponse toBankResponseMapper(Bank bank);

}