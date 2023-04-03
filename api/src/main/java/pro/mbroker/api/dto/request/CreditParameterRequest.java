package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class CreditParameterRequest {
    @NonNull
    private BigDecimal minMortgageSum;

    @NonNull
    private BigDecimal maxMortgageSum;

    @NonNull
    private Integer minCreditTerm;

    @NonNull
    private Integer maxCreditTerm;

    @NonNull
    private Integer minDownPayment;

    @NonNull
    private Integer maxDownPayment;

    @NonNull
    private Boolean isMaternalCapital;
}
