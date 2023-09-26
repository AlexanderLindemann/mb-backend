package pro.mbroker.api.dto;

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
public class EmployerDto {
    private String name;

    private Long inn;

    private Branch branch;

    private NumberOfEmployees numberOfEmployees;

    private OrganizationAge organizationAge;

    private List<UUID> salaryBank;

    private String phone;

    private String site;

    private TotalWorkExperience workExperience;

    private String position;

    private String address;

}
