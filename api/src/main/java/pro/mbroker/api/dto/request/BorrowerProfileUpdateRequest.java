package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.BorrowerRealEstateDto;
import pro.mbroker.api.dto.BorrowerVehicleDto;
import pro.mbroker.api.dto.EmployerDto;
import pro.mbroker.api.enums.Education;
import pro.mbroker.api.enums.EmploymentStatus;
import pro.mbroker.api.enums.FamilyRelation;
import pro.mbroker.api.enums.Gender;
import pro.mbroker.api.enums.MaritalStatus;
import pro.mbroker.api.enums.MarriageContract;
import pro.mbroker.api.enums.ProofOfIncome;
import pro.mbroker.api.enums.RegistrationType;
import pro.mbroker.api.enums.TotalWorkExperience;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@SuppressWarnings("PMD")
public class BorrowerProfileUpdateRequest {

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private String tin;

    private String prevFullName;

    private String passportNumber;

    private String passportIssuedByCode;

    private String passportIssuedByName;

    private String registrationAddress;

    private String residenceAddress;

    private String snils;

    private String residencyOutsideRU;

    private String longTermStayOutsideRU;

    private String taxResidencyCountries;

    private String publicOfficialPosition;

    private String tinForeign;

    private String birthPlace;

    private String citizenship;

    private Integer age;

    private Integer children;

    private Integer mainIncome;

    private Integer additionalIncome;

    private Integer pension;

    private Boolean residenceRF;

    private Boolean isPublicOfficial;

    private LocalDate birthdate;

    private LocalDate passportIssuedDate;

    private FamilyRelation relatedPublicOfficial;

    private MarriageContract marriageContract;

    private Education education;

    private EmploymentStatus employmentStatus;

    private RegistrationType registrationType;

    private MaritalStatus maritalStatus;

    private TotalWorkExperience totalWorkExperience;

    private Gender gender;

    private ProofOfIncome proofOfIncome;

    private FamilyRelation familyRelation;

    private EmployerDto employer;

    private BorrowerRealEstateDto realEstate;

    private BorrowerVehicleDto vehicle;
}
