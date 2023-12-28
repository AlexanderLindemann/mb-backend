package pro.mbroker.app.service;

import pro.mbroker.api.enums.RegionType;

import java.util.List;

public interface RegionService {
   List<RegionType> getRegionTypesByCianIdIn (List<Integer> cianIds);

   List<Integer> getRegionIdsByGroupName (String groupName);
}
