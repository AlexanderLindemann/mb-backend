package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mortgage_calculation")
public class MortgageCalculation extends BaseEntity {

    @Column(name = "real_estate_price", nullable = false)
    private BigDecimal realEstatePrice;

    @Column(name = "down_payment", nullable = false)
    private BigDecimal downPayment;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(name = "credit_term", nullable = false)
    private Integer monthCreditTerm;

}
