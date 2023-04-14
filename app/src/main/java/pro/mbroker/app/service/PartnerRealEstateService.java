package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.RealEstateAddressRequest;
import pro.mbroker.app.entity.RealEstateAddress;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.UUID;

public interface PartnerRealEstateService {

    RealEstateAddress addRealEstateAddress(UUID partnerId, RealEstateAddressRequest request);

    void deleteRealEstateAddress(UUID addressId);

    RealEstateAddress updateRealEstateAddress(UUID addressId, RealEstateAddressRequest request);

    List<RealEstateAddress> getRealEstateAddressByPartnerId(Pagination pagination, UUID partnerId);
}
