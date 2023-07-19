package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class LoanProgramCalculationDto {
    private UUID creditProgramId;

    private String creditProgramName;

    private Double baseRate;

    private BigDecimal monthlyPayment;

    private BigDecimal overpayment;

    private SalaryClientProgramCalculationDto salaryClientCalculation;

}
