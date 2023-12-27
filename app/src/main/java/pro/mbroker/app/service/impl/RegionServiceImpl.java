package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.repository.RegionRepository;
import pro.mbroker.app.service.RegionService;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    @Override
    public List<RegionType> getRegionTypesByCianIdIn(List<Integer> cianIds) {
        return regionRepository.findRegionTypesByCianIdIn(cianIds);
    }
}
