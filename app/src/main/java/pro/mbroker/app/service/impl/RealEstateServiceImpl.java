package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.RealEstateRepository;
import pro.mbroker.app.service.RealEstateService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealEstateServiceImpl implements RealEstateService {
    private final RealEstateRepository realEstateRepository;

    @Override
    public RealEstate findById(UUID realEstateId) {
        return realEstateRepository.findById(realEstateId)
                .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, realEstateId));
    }
}
