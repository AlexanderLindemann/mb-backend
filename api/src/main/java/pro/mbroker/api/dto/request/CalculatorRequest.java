package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CalculatorRequest {
    @NotNull
    private UUID realEstateId;
    @NotNull
    private CreditPurposeType creditPurposeType;
    @NotNull
    private RealEstateType realEstateType;

    private BigDecimal mortgageSum;

    private BigDecimal downPayment;

    private Integer maxMonthlyPayment;

    private Integer creditTerm;

    private Boolean isMaternalCapital;
}
