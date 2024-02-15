package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.app.entity.Bank;

@Mapper(uses = {CreditProgramDetailMapper.class})
public interface BankMapper {
    @Mapping(target = "creditProgram", ignore = true)
    @Mapping(target = "storageResponse", ignore = true)
    BankResponse toBankResponseMapper(Bank bank);
}