package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.UUID;

public interface PartnerRealEstateService {

    RealEstate addRealEstate(UUID partnerId, RealEstateRequest request);

    void deleteRealEstate(UUID addressId);

    RealEstate updateRealEstate(UUID addressId, RealEstateRequest request);

    List<RealEstate> getRealEstateByPartnerId(Pagination pagination, UUID partnerId);

    List<RealEstate> getCurrentRealEstate(Pagination pagination);
}
