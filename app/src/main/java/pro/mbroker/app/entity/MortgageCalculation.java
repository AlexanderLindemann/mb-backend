package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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

    @Column(name = "is_maternal_capital", nullable = false)
    private Boolean isMaternalCapital;

    @ManyToMany
    @JoinTable(
            name = "mortgage_calculator_salary_bank",
            joinColumns = @JoinColumn(name = "mortgage_calculation_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_id")
    )
    private List<Bank> salaryBanks;
}
