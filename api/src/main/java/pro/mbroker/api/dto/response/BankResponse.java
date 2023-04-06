package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankResponse {
    private UUID id;

    private String name;

    private Integer orderNumber;

    private List<BankContactResponse> contacts;

    private List<CreditProgramResponse> creditProgram;
}
