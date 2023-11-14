package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.BorrowerRealEstateDto;
import pro.mbroker.api.dto.BorrowerVehicleDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerProfileForUpdateResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private String prevFullName;

    private String taxResidencyCountries;

    private String residencyOutsideRU;

    private String publicOfficialPosition;

    private String longTermStayOutsideRU;

    private String tinForeign;

    private String birthPlace;

    private String citizenship;

    private String passportNumber;

    private String passportIssuedByCode;

    private String passportIssuedByName;

    private String registrationAddress;

    private String residenceAddress;

    private String snils;

    private String tin;

    private Integer age;

    private Integer children;

    private Integer mainIncome;

    private Integer additionalIncome;

    private Integer pension;

    private Boolean residenceRF;

    private Boolean isPublicOfficial;

    private LocalDateTime createdAt;

    private LocalDate birthdate;

    private BorrowerProfileStatus status;

    private Gender gender;

    private MaritalStatus maritalStatus;

    private LocalDate passportIssuedDate;

    private FamilyRelation relatedPublicOfficial;

    private MarriageContract marriageContract;

    private Education education;

    private EmploymentStatus employmentStatus;

    private RegistrationType registrationType;

    private TotalWorkExperience totalWorkExperience;

    private ProofOfIncome proofOfIncome;

    private FamilyRelation familyRelation;

    private BorrowerEmployerResponse employer;

    private BorrowerRealEstateDto realEstate;

    private BorrowerVehicleDto vehicle;

    private List<BorrowerDocumentResponse> documents;

}
