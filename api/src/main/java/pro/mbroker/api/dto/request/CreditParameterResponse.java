package pro.mbroker.api.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditParameterResponse {
    private BigDecimal minMortgageSum;

    private BigDecimal maxMortgageSum;

    private Integer minCreditTerm;

    private Integer maxCreditTerm;

    private BigDecimal minDownPayment;

    private BigDecimal maxDownPayment;

    private Boolean isMaternalCapital;
}
