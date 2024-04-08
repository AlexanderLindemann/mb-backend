package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.PartnerContactRequest;
import pro.mbroker.app.entity.PartnerContact;

import java.util.List;
import java.util.UUID;

public interface PartnerContactService {

    PartnerContact addPartnerContact(UUID partnerId, Integer sdId, PartnerContactRequest request);

    PartnerContact editPartnerContact(UUID id, Integer sdId, PartnerContactRequest request);

    void deletePartnerContact(UUID id, Integer sdId);

    List<PartnerContact> getPartnerContacts(UUID partnerId);
}