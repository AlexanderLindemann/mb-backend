package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "partner_application")
public class PartnerApplication extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner;

    @Column(name = "fullName")
    private String borrowerFullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_purpose_type", nullable = false)
    private CreditPurposeType creditPurposeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "real_estate_type", nullable = false)
    private RealEstateType realEstateType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "real_estate_id", referencedColumnName = "id")
    private RealEstate realEstate;

    @OneToMany(mappedBy = "partnerApplication", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BorrowerApplication> borrowerApplications;
}