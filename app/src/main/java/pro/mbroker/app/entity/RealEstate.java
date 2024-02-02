package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.RegionType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@ToString()
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "real_estate")
public class RealEstate extends BaseEntity {

    @Column(name = "residential_complex_name", nullable = false, length = 1000)
    private String residentialComplexName;

    @Column(name = "region", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegionType region;

    @Column(name = "address", length = 1000)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @Column(name = "cian_id")
    private Integer cianId;
}
