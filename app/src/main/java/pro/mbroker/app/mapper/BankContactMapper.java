package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.app.model.bank.BankContact;

@Mapper(
        componentModel = "spring"
)
public interface BankContactMapper {
    BankContact toBankContactMapper(BankContactRequest bankContactRequest);

}