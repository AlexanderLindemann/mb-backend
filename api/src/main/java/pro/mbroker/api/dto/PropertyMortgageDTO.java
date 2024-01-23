package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class PropertyMortgageDTO {

    private BigDecimal realEstatePrice;

    private BigDecimal downPayment;

    private Integer monthCreditTerm;

    private List<BankLoanProgramDto> bankLoanProgramDto;

    private List<LoanProgramCalculationDto> loanProgramCalculationDto = new ArrayList<>();
}
