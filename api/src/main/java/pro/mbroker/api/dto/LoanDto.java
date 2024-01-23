package pro.mbroker.api.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoanDto {

    @CsvBindByName(column = "id")
    private Integer id;

    @CsvBindByName(column = "bank_id")
    private Integer bankId;

    @CsvBindByName(column = "base_rate")
    private Double baseRate;

    @CsvBindByName(column = "region_id")
    private String regionIds;

    @CsvBindByName(column = "region_group")
    private String regionGroup;

    @CsvBindByName(column = "mortgage_type")
    private String mortgageType;

    @CsvBindByName(column = "real_estate_type")
    private String realEstateType;

    @CsvBindByName(column = "object_type")
    private String objectType;

    @CsvBindByName(column = "benefit_program")
    private String benefitProgram;

    @CsvBindByName(column = "loan_term_min")
    private Integer loanTermMin;

    @CsvBindByName(column = "loan_term_max")
    private Integer loanTermMax;

    @CsvBindByName(column = "income_confirmation")
    private String incomeConfirmation;

    @CsvBindByName(column = "loan_amount_min")
    private Long loanAmountMin;

    @CsvBindByName(column = "loan_amount_max")
    private Long loanAmountMax;

    @CsvBindByName(column = "down_payment_rate_min")
    private String downPaymentRateMin;

    @CsvBindByName(column = "down_payment_rate_max")
    private String downPaymentRateMax;

    @CsvBindByName(column = "has_life_insurance")
    private String hasLifeInsurance;

    @CsvBindByName(column = "is_not_citizen")
    private String isNotCitizen;

    @CsvBindByName(column = "employment_type")
    private String employmentType;

    @CsvBindByName(column = "registration_type")
    private String registrationType;

    @CsvBindByName(column = "is_active")
    private String isActive;

    @CsvBindByName(column = "created_at")
    private String createdAt;

    @CsvBindByName(column = "shadow_id")
    private Long shadowId;

}



