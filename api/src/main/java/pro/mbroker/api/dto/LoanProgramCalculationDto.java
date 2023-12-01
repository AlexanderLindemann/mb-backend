package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class LoanProgramCalculationDto {

    private UUID bankId;

    private UUID creditProgramId;

    private String creditProgramName;

    private List<RealEstateType> realEstateTypes;

    private Double baseRate;

    private BigDecimal monthlyPayment;

    private BigDecimal overpayment;

    private int creditTerm;

    private SalaryClientProgramCalculationDto salaryClientCalculation;

}
