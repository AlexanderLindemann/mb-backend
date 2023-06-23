package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BankRequest {
    private String name;

    private Long attachment_id;

    private List<BankContactRequest> bankContacts;

}
