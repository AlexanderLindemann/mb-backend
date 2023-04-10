package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.app.entity.Partner;

import java.util.List;
import java.util.UUID;

public interface PartnerService {

    Partner createPartner(PartnerRequest request);

    Partner getPartner(UUID partnerId);

    List<Partner> getAllPartner(int page, int size, String sortBy, String sortOrder);
}
