package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.ApplicationStatus;

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

    private BorrowerProfileResponse mainBorrower;

    private List<BorrowerProfileResponse> coBorrowers;

    private ApplicationStatus applicationStatus;

    private LocalDateTime updatedAt;

    private BigDecimal mortgageSum;

    private BigDecimal monthlyPayment;

    private BigDecimal downPayment;

    private Integer monthCreditTerm;

    private BigDecimal overpayment;

    private Double interestRate;

}