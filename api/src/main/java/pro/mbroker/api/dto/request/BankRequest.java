package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankRequest {
    private String name;

    private UUID fileStorageId;

    private List<BankContactRequest> bankContacts;

    private Integer cianBankId;
}
