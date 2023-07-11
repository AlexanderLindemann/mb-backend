package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class MortgageCalculationDto {

    private BigDecimal realEstatePrice;

    private BigDecimal downPayment;

    private BigDecimal monthlyPayment;

    private Integer creditTerm;

    private Boolean isMaternalCapital;

    private List<UUID> salaryBanks;

}
