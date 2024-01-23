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
@Table(name = "cian_additional_rate_rules")
public class CianAdditionalRateRules extends BaseEntity{
    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "condition")
    private String condition;

    @Column(name = "rate", precision = 10, scale = 2)
    private BigDecimal rate;

    @Column(name = "region_id", columnDefinition = "TEXT")
    private String regionId;

    @Column(name = "mortgage_type", columnDefinition = "TEXT")
    private String mortgageType;

    @Column(name = "real_estate_type", columnDefinition = "TEXT")
    private String realEstateType;

    @Column(name = "object_type", columnDefinition = "TEXT")
    private String objectType;

    @Column(name = "benefit_program", columnDefinition = "TEXT")
    private String benefitProgram;

    @Column(name = "employment_type", columnDefinition = "TEXT")
    private String employmentType;

    @Column(name = "loan_amount_min")
    private Long loanAmountMin;

    @Column(name = "loan_amount_max")
    private Long loanAmountMax;

    @Column(name = "shadow_id")
    private Long shadowId;
}
