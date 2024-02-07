package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankLoanProgramDto {
    private UUID bankId;

    private String bankName;

    private URL logo;

}
