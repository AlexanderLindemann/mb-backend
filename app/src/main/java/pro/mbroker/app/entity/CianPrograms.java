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
@Table(name = "cian_programs")
public class CianPrograms extends BaseEntity{

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "base_rate")
    private BigDecimal baseRate;

    @Column(name = "region_id")
    private String regionId;

    @Column(name = "region_group")
    private String regionGroup;

    @Column(name = "mortgage_type")
    private String mortgageType;

    @Column(name = "real_estate_type")
    private String realEstateType;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "benefit_program")
    private String benefitProgram;

    @Column(name = "loan_term_min")
    private Integer loanTermMin;

    @Column(name = "loan_term_max")
    private Integer loanTermMax;

    @Column(name = "income_confirmation")
    private String incomeConfirmation;

    @Column(name = "loan_amount_min")
    private Long loanAmountMin;

    @Column(name = "loan_amount_max")
    private Long loanAmountMax;

    @Column(name = "down_payment_rate_min")
    private BigDecimal downPaymentRateMin;

    @Column(name = "down_payment_rate_max")
    private BigDecimal downPaymentRateMax;

    @Column(name = "has_life_insurance")
    private String hasLifeInsurance;

    @Column(name = "is_not_citizen")
    private String isNotCitizen;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "registration_type")
    private String registrationType;

    @Column(name = "shadow_id")
    private Long shadowId;

}
