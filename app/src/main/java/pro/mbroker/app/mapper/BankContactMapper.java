package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.app.model.bank.BankContact;

@Mapper(componentModel = "spring")
public interface BankContactMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bank", ignore = true)
    BankContact toBankContactMapper(BankContactRequest bankContactRequest);

}