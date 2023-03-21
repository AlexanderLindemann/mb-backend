package pro.mbroker.api.dto;

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

    private String logo;

    private List<BankContactResponse> contacts;
}
