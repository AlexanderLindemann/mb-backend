package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import pro.mbroker.api.dto.response.BankContactResponse;
import pro.mbroker.app.entity.BankContact;

import java.util.List;

@Mapper
public interface BankContactMapper {
    BankContactResponse toBankContactResponseMapper(BankContact bankContact);

    List<BankContactResponse> toBankContactResponseListMapper(List<BankContact> bankContacts);

}