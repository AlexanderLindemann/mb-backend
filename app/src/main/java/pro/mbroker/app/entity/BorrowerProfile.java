package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.BorrowerProfileStatus;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_profile")
public class BorrowerProfile extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "borrower_application_status")
    private BorrowerProfileStatus borrowerProfileStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_application_id", referencedColumnName = "id")
    private PartnerApplication partnerApplication;

    @OneToMany(mappedBy = "borrowerProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BorrowerDocument> borrowerDocument;
}
