package pro.mbroker.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "region_group_mappings")
public class RegionGroupMappings {

    @Id
    private Long id;

    @Column(name = "region_group", nullable = false, length = 1000)
    private String regionGroup;

    @Column(name = "region_id", nullable = false)
    private Integer regionId;
}
