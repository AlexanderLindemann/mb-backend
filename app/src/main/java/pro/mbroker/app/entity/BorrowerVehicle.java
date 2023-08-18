package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.BasisOfOwnership;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrower_vehicle")
public class BorrowerVehicle extends BaseEntity {

    @Column(name = "model", nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "basis_of_ownership")
    private BasisOfOwnership basisOfOwnership;

    @Column(name = "year_of_manufacture", length = 4)
    private Integer yearOfManufacture;

    @Column(name = "price")
    private Integer price;

    @Column(name = "is_collateral", nullable = false, columnDefinition = "boolean default false")
    private Boolean isCollateral = false;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private BorrowerProfile borrowerProfile;
}


