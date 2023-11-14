package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.Education;
import pro.mbroker.api.enums.EmploymentStatus;
import pro.mbroker.api.enums.FamilyRelation;
import pro.mbroker.api.enums.Gender;
import pro.mbroker.api.enums.MaritalStatus;
import pro.mbroker.api.enums.MarriageContract;
import pro.mbroker.api.enums.ProofOfIncome;
import pro.mbroker.api.enums.RegistrationType;
import pro.mbroker.api.enums.TotalWorkExperience;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_profile")
public class BorrowerProfile extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "borrower_application_status")
    @ColumnDefault("'DATA_NO_ENTERED'")
    private BorrowerProfileStatus borrowerProfileStatus;

    @Column(name = "prev_full_name")
    private String prevFullName;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "children")
    private Integer children;

    @Enumerated(EnumType.STRING)
    @Column(name = "marriage_contract")
    private MarriageContract marriageContract;

    @Enumerated(EnumType.STRING)
    @Column(name = "education")
    private Education education;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "residency_outside_ru")
    private String residencyOutsideRU;

    @Column(name = "public_official_position")
    private String publicOfficialPosition;

    @Column(name = "long_term_stay_outside_ru")
    private String longTermStayOutsideRU;

    @Column(name = "is_public_official")
    private Boolean isPublicOfficial;

    @Column(name = "tin")
    private String tin;

    @Column(name = "tin_foreign")
    private String tinForeign;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "passport_issued_date")
    private LocalDate passportIssuedDate;

    @Column(name = "passport_issued_by_code")
    private String passportIssuedByCode;

    @Column(name = "passport_issued_by_name")
    private String passportIssuedByName;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "registration_address")
    private String registrationAddress;

    @Column(name = "residence_address")
    private String residenceAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type")
    private RegistrationType registrationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_relation")
    private FamilyRelation familyRelation;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_public_official")
    private FamilyRelation relatedPublicOfficial;

    @Column(name = "snils")
    private String snils;

    @Column(name = "tax_residency_countries")
    private String taxResidencyCountries;

    @Column(name = "residence_rf")
    private Boolean residenceRF = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "total_work_experience")
    private TotalWorkExperience totalWorkExperience;

    @Column(name = "main_income")
    private Integer mainIncome;

    @Column(name = "additional_income")
    private Integer additionalIncome;

    @Column(name = "pension")
    private Integer pension;

    @Enumerated(EnumType.STRING)
    @Column(name = "proof_of_income")
    private ProofOfIncome proofOfIncome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_application_id", referencedColumnName = "id")
    private PartnerApplication partnerApplication;

    @OneToMany(mappedBy = "borrowerProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BorrowerDocument> borrowerDocument;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "borrower_employer_id")
    private BorrowerEmployer employer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "borrower_real_estate_id")
    private BorrowerRealEstate realEstate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "borrower_vehicle_id")
    private BorrowerVehicle vehicle;

}
