package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.OrganizationAge;
import pro.mbroker.api.enums.TotalWorkExperience;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_employer")
public class BorrowerEmployer extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "TIN", nullable = false)
    private String tin;

    @Enumerated(EnumType.STRING)
    @Column(name = "branch", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "number_of_employees", nullable = false)
    private NumberOfEmployees numberOfEmployees;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_age", nullable = false)
    private OrganizationAge organizationAge;

    @ManyToMany()
    @JoinTable(
            name = "employer_bank_relation",
            joinColumns = @JoinColumn(name = "employer_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_id")
    )
    private Set<Bank> salaryBanks = new HashSet<>();


    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "site")
    private String site;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_experience", nullable = false)
    private TotalWorkExperience workExperience;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "address")
    private String address;

    @Column(name = "bank_details")
    private String bankDetails;

    @Column(name = "manager")
    private String manager;

    @Column(name = "is_current_employer")
    private Boolean isCurrentEmployer;

    @OneToOne(mappedBy = "employer", cascade = CascadeType.ALL)
    private BorrowerProfile borrowerProfile;
}

