package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
public class Bank extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "attachment_id", referencedColumnName = "id")
    private Attachment attachment;

    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "bank", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<BankContact> contacts;

    @OneToMany(mappedBy = "bank", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<CreditProgram> creditPrograms;

    @ManyToMany
    @JoinTable(
            name = "employer_bank_relation",
            joinColumns = @JoinColumn(name = "bank_id"),
            inverseJoinColumns = @JoinColumn(name = "employer_id")
    )
    private Set<BorrowerEmployer> employers;
}
