package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.app.entity.RealEstate;

import java.util.List;
import java.util.UUID;

public interface PartnerRealEstateService {

    RealEstate addRealEstate(UUID partnerId, RealEstateRequest request);

    void deleteRealEstate(UUID addressId);

    RealEstate updateRealEstate(UUID addressId, RealEstateRequest request);

    List<RealEstate> getRealEstateByPartnerId(int page, int size, String sortBy, String sortOrder, UUID partnerId);

    List<RealEstate> getCurrentRealEstate(int page, int size, String sortBy, String sortOrder);
}
