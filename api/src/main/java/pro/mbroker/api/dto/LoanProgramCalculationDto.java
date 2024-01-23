package pro.mbroker.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class LoanProgramCalculationDto {

    private int creditTerm;

    private UUID bankId;

    private UUID creditProgramId;

    private String creditProgramName;

    private Double baseRate;

    private BigDecimal monthlyPayment;

    private BigDecimal overpayment;

    private String description;

    private String fullDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BankApplicationStatus status;

    private RealEstateType realEstateType;

    private SalaryClientProgramCalculationDto salaryClientCalculation;
}
