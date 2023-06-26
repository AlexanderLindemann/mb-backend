package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BankContactRequest {
    
    private UUID id;

    private String fullName;

    private String email;
}
