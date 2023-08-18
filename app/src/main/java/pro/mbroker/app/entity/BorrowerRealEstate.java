package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.BasisOfOwnership;
import pro.mbroker.api.enums.RealEstateType;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_real_estate")
public class BorrowerRealEstate extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RealEstateType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "basis_of_ownership")
    private BasisOfOwnership basisOfOwnership;

    @Column(name = "share")
    private Integer share;

    @Column(name = "area")
    private Float area;

    @Column(name = "price")
    private Integer price;

    @Column(name = "address")
    private String address;

    @Column(name = "is_collateral", nullable = false, columnDefinition = "boolean default false")
    private Boolean isCollateral = false;

    @OneToOne(mappedBy = "realEstate", cascade = CascadeType.ALL)
    private BorrowerProfile borrowerProfile;
}

