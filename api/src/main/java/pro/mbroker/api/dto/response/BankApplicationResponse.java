package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.SalaryClientProgramCalculationDto;
import pro.mbroker.api.dto.request.CreditParameterResponse;
import pro.mbroker.api.dto.request.notification.UnderwritingResponse;
import pro.mbroker.api.enums.BankApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankApplicationResponse {
    private UUID id;

    private UUID creditProgramId;

    private CreditParameterResponse creditParameter;

    private Integer applicationNumber;

    private BorrowerProfileResponse mainBorrower;

    private List<BorrowerProfileResponse> coBorrowers;

    private BankApplicationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BigDecimal mortgageSum;

    private BigDecimal monthlyPayment;

    private BigDecimal downPayment;

    private Integer creditTerm;

    private BigDecimal overpayment;

    private Double baseRate;

    private String creditProgramName;

    private SalaryClientProgramCalculationDto salaryClientCalculation;

    private UnderwritingResponse underwriting;

}
