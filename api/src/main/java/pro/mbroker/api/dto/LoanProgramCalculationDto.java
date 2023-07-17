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

    @DecimalMin(value = "0.00", inclusive = true, message = "Calculated rate cannot be negative")
    @DecimalMax(value = "100.00", inclusive = true, message = "Calculated rate cannot be greater than 100.00")
    private Double calculatedRate;

    private Double baseRate;

    private Double salaryBankRate;

    private BigDecimal monthlyPayment;

    private BigDecimal overpayment;

}
