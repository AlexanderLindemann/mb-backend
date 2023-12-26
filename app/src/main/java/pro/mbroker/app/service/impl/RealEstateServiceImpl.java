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
    public RealEstate findByRealEstateId(String realEstateId) {
        Object realEstateIdParsed = parseStringToUUIDOrInteger(realEstateId);
        RealEstate realEstate = null;
        if (realEstateIdParsed instanceof UUID) {
            realEstate = realEstateRepository.findById((UUID) realEstateIdParsed)
                    .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, realEstateId));
        } else if (realEstateIdParsed instanceof Integer) {
            realEstate = realEstateRepository.findAllByCianId((Integer) realEstateIdParsed)
                    //TODO убрать когда будет cianId уникальным в БД
                    .stream().findFirst()
                    .orElseThrow(() -> new ItemNotFoundException(RealEstate.class, realEstateId));
        }
        return realEstate;
    }

    private Object parseStringToUUIDOrInteger(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
