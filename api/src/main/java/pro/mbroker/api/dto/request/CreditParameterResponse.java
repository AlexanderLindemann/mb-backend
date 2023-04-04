package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class CreditParameterResponse {
    private BigDecimal minMortgageSum;

    private BigDecimal maxMortgageSum;

    private Integer minCreditTerm;

    private Integer maxCreditTerm;

    private Integer minDownPayment;

    private Integer maxDownPayment;

    private Boolean isMaternalCapital;
}
