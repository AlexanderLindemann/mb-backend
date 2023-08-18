package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.BorrowerRealEstateDto;
import pro.mbroker.api.dto.BorrowerVehicleDto;
import pro.mbroker.api.dto.EmployerDto;
import pro.mbroker.api.enums.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class BorrowerProfileUpdateRequest {

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private BorrowerProfileStatus status;

    private String prevFullName;

    private LocalDate birthdate;

    private Integer age;

    private Gender gender;

    private MaritalStatus maritalStatus;

    private Integer children;

    private Boolean marriageContract;

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

    private int mainIncome;

    private int additionalIncome;

    private int pension;

    private ProofOfIncome proofOfIncome;

    private EmployerDto employer;

    private BorrowerRealEstateDto realEstate;

    private BorrowerVehicleDto vehicle;

}
