package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.OrganizationAge;
import pro.mbroker.api.enums.TotalWorkExperience;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerEmployerRequest {
    private String name;

    private String tin;

    private String position;

    private String address;

    private String phone;

    private String site;

    private String bankDetails;

    private String manager;

    private Boolean isCurrentEmployer;

    private Branch branch;

    private NumberOfEmployees numberOfEmployees;

    private OrganizationAge organizationAge;

    private TotalWorkExperience workExperience;

    private List<UUID> salaryBanks;

}
