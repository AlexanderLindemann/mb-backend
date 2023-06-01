package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class MortgageCalculationDto {

    private BigDecimal realEstatePrice;

    private BigDecimal downPayment;

    private BigDecimal monthlyPayment;

    private Integer creditTerm;

    private Boolean isMaternalCapital;

}
