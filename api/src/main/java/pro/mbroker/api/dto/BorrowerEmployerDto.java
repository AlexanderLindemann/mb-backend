package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.OrganizationAge;
import pro.mbroker.api.enums.TotalWorkExperience;

import java.util.Set;

@Getter
@Setter
@ToString
public class BorrowerEmployerDto {

    private String name;

    private Long inn;

    private Branch branch;

    private NumberOfEmployees numberOfEmployees;

    private OrganizationAge organizationAge;

    private Set<BankResponse> salaryBanks;

    private String phone;

    private String site;

    private TotalWorkExperience workExperience;

    private String position;

    private String address;
}
