package pro.mbroker.app.service;

import pro.mbroker.app.entity.RealEstate;

public interface RealEstateService {

    RealEstate findByRealEstateId(String realEstateId);
}
