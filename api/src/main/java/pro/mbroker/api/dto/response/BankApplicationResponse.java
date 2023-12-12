package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.SalaryClientProgramCalculationDto;
import pro.mbroker.api.dto.request.CreditParameterResponse;
import pro.mbroker.api.dto.request.notification.UnderwritingResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankApplicationResponse {

    private Integer applicationNumber;

    private Double baseRate;

    private String creditProgramName;

    private Integer creditTerm;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BigDecimal mortgageSum;

    private BigDecimal monthlyPayment;

    private BigDecimal downPayment;

    private BigDecimal overpayment;

    private UUID id;

    private UUID creditProgramId;

    private RealEstateType realEstateType;

    private CreditParameterResponse creditParameter;

    private BorrowerProfileResponse mainBorrower;

    private BankApplicationStatus status;

    private SalaryClientProgramCalculationDto salaryClientCalculation;

    private UnderwritingResponse underwriting;

    private List<BorrowerProfileResponse> coBorrowers;

}
