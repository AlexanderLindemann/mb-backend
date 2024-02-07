package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankResponse {

    private UUID id;

    private String name;

    private Integer orderNumber;

    private URL logo;

    private List<BankContactResponse> contacts;

    private List<CreditProgramResponse> creditProgram;

    private Integer cianId;
}
