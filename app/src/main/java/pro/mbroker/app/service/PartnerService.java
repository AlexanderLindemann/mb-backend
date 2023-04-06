package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.app.entity.Partner;

public interface PartnerService {

    Partner createPartner(PartnerRequest request);
}
