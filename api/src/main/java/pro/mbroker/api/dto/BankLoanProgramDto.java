package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankLoanProgramDto {
    private UUID bankId;

    private String bankName;

    private String logo;

    private List<LoanProgramCalculationDto> loanProgramCalculationDto = new ArrayList<>();


}
