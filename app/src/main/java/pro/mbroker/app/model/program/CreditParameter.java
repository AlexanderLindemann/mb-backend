package pro.mbroker.app.model.program;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_parameter")
@EqualsAndHashCode(of = {"minMortgageSum", "maxMortgageSum", "minCreditTerm", "maxCreditTerm", "minDownPayment", "maxDownPayment", "isMaternalCapital"})
public class CreditParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "min_mortgage_sum", nullable = false, columnDefinition = "decimal(10, 2) default 0.00")
    private BigDecimal minMortgageSum;

    @Column(name = "max_mortgage_sum", nullable = false, columnDefinition = "decimal(10, 2) default 0.00")
    private BigDecimal maxMortgageSum;

    @Column(name = "min_credit_term", nullable = false, columnDefinition = "int default 12")
    private Integer minCreditTerm;

    @Column(name = "max_credit_term", nullable = false, columnDefinition = "int default 360")
    private Integer maxCreditTerm;

    @Column(name = "min_down_payment", nullable = false, columnDefinition = "int default 20")
    private Integer minDownPayment;

    @Column(name = "max_down_payment", nullable = false, columnDefinition = "int default 100")
    private Integer maxDownPayment;

    @Column(name = "is_maternal_capital", nullable = false, columnDefinition = "boolean default false")
    private Boolean isMaternalCapital;

}
