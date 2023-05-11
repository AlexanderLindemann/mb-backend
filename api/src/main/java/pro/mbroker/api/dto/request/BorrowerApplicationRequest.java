package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerApplicationRequest {

    private UUID id;

    private UUID creditProgramId;

    private BigDecimal monthlyPayment;

    private BigDecimal realEstatePrice;

    private BigDecimal downPayment;

    private Integer monthCreditTerm;

    private BigDecimal overpayment;

}
