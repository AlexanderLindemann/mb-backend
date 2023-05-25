package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class BankLoanProgramDto {
    private String bankName;

    private String logo;

    private List<LoanProgramCalculationDto> loanProgramCalculationDto = new ArrayList<>();


}
