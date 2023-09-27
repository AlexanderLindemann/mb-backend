package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.BorrowerEmployerDto;
import pro.mbroker.api.dto.BorrowerRealEstateDto;
import pro.mbroker.api.dto.BorrowerVehicleDto;
import pro.mbroker.api.enums.Education;
import pro.mbroker.api.enums.EmploymentStatus;
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

    private String prevFullName;

    private LocalDate birthdate;

    private Integer age;

    private Gender gender;

    private MaritalStatus maritalStatus;

    private Integer children;

    private MarriageContract marriageContract;

    private Education education;

    private String passportNumber;

    private LocalDate passportIssuedDate;

    private String passportIssuedByCode;

    private String passportIssuedByName;

    private String registrationAddress;

    private String residenceAddress;

    private RegistrationType registrationType;

    private String snils;

    private Boolean residenceRF;

    private EmploymentStatus employmentStatus;

    private TotalWorkExperience totalWorkExperience;

    private Integer mainIncome;

    private Integer additionalIncome;

    private Integer pension;

    private ProofOfIncome proofOfIncome;

    private BorrowerEmployerDto employer;

    private BorrowerRealEstateDto realEstate;

    private BorrowerVehicleDto vehicle;
}
