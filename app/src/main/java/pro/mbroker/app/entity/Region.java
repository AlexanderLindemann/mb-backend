package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.mbroker.api.enums.RegionType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "region")
public class Region extends BaseEntity{
    @Column(name = "name", nullable = false, length = 1000)
    private String name;

    @Column (name = "region_type")
    @Enumerated(EnumType.STRING)
    private RegionType regionType;

    @Column (name = "cian_id")
    private Integer cianId;
}
