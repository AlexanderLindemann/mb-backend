package pro.mbroker.app.service;

import pro.mbroker.app.entity.RealEstate;

import java.util.UUID;

public interface RealEstateService {

    RealEstate findById(UUID realEstateId);
}
