package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.UUID;

public interface PartnerRealEstateService {

    RealEstate addRealEstateAddress(UUID partnerId, RealEstateRequest request);

    void deleteRealEstateAddress(UUID addressId);

    RealEstate updateRealEstateAddress(UUID addressId, RealEstateRequest request);

    List<RealEstate> getRealEstateAddressByPartnerId(Pagination pagination, UUID partnerId);
}
