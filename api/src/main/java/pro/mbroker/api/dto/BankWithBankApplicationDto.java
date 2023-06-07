package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.response.BankApplicationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankWithBankApplicationDto {
    private UUID bankId;

    private String bankName;

    private String logo;

    private List<BankApplicationResponse> bankApplications = new ArrayList<>();

}
