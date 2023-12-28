package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.Region;

import java.util.List;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID>, JpaSpecificationExecutor<Region> {
    @Query("SELECT r.regionType FROM Region r WHERE r.cianId IN :regionCianIds")
    List<RegionType> findRegionTypesByCianIdIn(List<Integer> regionCianIds);

    @Query("SELECT rgm.regionId FROM RegionGroupMappings rgm WHERE rgm.regionGroup = :groupName")
    List<Integer> getRegionIdsByGroupName(@Param("groupName") String groupName);

}

