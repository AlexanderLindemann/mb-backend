package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    @Table(name = "cian_bank_future_rules")
    public class CianBankFutureRules extends BaseEntity  {

        @Column(name = "bank_id")
        private Integer bankId;

        @Column(name = "region_id")
        private String regionId;

        @Column(name = "mortgage_type")
        private String mortgageType;

        @Column(name = "real_estate_type")
        private String realEstateType;

        @Column(name = "object_type")
        private String objectType;

        @Column(name = "benefit_program")
        private String benefitProgram;

        @Column(name = "employment_type")
        private String employmentType;

        @Column(name = "min_age")
        private Integer minAge;

        @Column(name = "max_age")
        private Integer maxAge;

        @Column(name = "min_finally_age")
        private Integer minFinallyAge;

        @Column(name = "max_finally_age")
        private Integer maxFinallyAge;

        @Column(name = "feature_description")
        private String featureDescription;

        @Column(name = "shadow_id")
        private Long shadowId;
}
