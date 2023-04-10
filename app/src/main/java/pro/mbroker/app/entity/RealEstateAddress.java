package pro.mbroker.app.entity;

import lombok.*;
import pro.mbroker.api.enums.RegionType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "real_estate_address")
public class RealEstateAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    //Название ЖК
    @Column(name = "residential_complex_name", nullable = false, length = 100)
    private String residentialComplexName;

    //Регион
    @Column(name = "region", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegionType region;
    //Адрес
    @Column(name = "address", length = 100)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

}
