package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerApplicationResponse {
    private UUID id;

    private String bankName;

    private String creditProgramName;

    private ApplicationStatus applicationStatus;

    private LocalDateTime updatedAt;

    private BigDecimal monthlyPayment;

    private BigDecimal downPayment;

    private Integer monthCreditTerm;

    private BigDecimal overpayment;

    private Double interestRate;

}
