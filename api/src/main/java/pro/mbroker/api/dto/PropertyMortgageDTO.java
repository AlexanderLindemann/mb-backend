package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class PropertyMortgageDTO {
    private BigDecimal mortgageSum;

    private BigDecimal downPayment;

    private Integer creditTerm;

    private List<BankLoanProgramDto> bankLoanProgramDto;

}
