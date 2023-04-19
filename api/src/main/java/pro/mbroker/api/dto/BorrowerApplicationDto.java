package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerApplicationDto {
    private UUID id;

    private String bankName;

    private String creditProgramName;

    private ApplicationStatus applicationStatus;

    private ZonedDateTime lastEditDate;

    private BigDecimal monthlyPayment;

    private BigDecimal downPayment;

    private Integer monthCreditTerm;

    private BigDecimal overpayment;

    private Double interestRate;

}
