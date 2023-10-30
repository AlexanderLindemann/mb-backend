package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.OrganizationAge;
import pro.mbroker.api.enums.TotalWorkExperience;

import java.util.Set;

@Getter
@Setter
@ToString
public class BorrowerEmployerResponse {

    private String name;

    private String tin;

    private Branch branch;

    private NumberOfEmployees numberOfEmployees;

    private OrganizationAge organizationAge;

    private Set<BankResponse> salaryBanks;

    private String phone;

    private String site;

    private TotalWorkExperience workExperience;

    private String position;

    private String address;

    private String bankDetails;

    private String manager;

    private Boolean isCurrentEmployer;
}
