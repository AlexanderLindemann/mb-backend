package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.app.entity.Partner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerService {

    Partner createPartner(PartnerRequest request, Integer sdId);

    Partner getPartner(UUID partnerId);

    Partner getIsActivePartner(UUID partnerId);

    List<Partner> getAllPartner(int page, int size, String sortBy, String sortOrder);

    Partner updatePartnerById(UUID partnerId, PartnerRequest request, Integer sdId);

    List<Partner> getCurrentPartners(Integer organisationId);

    void deletePartner(UUID partnerId, Integer sdId);

    Partner getPartnerByPartnerApplicationId(UUID partnerApplicationId);

    Partner getPartnerByCianIdOrName(Integer cianId, String name);

    Optional<Partner> findPartnerByCianId(Integer cianId);

    Partner updateCianId(UUID partnerId, Integer cianId);

    Partner saveOrUpdatePartner(Partner partner);
}
