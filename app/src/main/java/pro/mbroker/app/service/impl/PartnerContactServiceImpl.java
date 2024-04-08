package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.request.PartnerContactRequest;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerContact;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.PartnerContactMapper;
import pro.mbroker.app.repository.PartnerContactRepository;
import pro.mbroker.app.repository.specification.PartnerContactSpecification;
import pro.mbroker.app.service.PartnerContactService;
import pro.mbroker.app.service.PartnerService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerContactServiceImpl implements PartnerContactService {

    private final PartnerContactRepository partnerContactRepository;
    private final PartnerService partnerService;
    private final PartnerContactMapper partnerContactMapper;

    @Override
    public PartnerContact addPartnerContact(UUID partnerId, Integer sdId, PartnerContactRequest request) {
        Partner partner = partnerService.getPartner(partnerId);
        PartnerContact partnerContact = partnerContactMapper.toPartnerContact(request);
        partnerContact.setPartner(partner);
        partnerContact.setCreatedBy(sdId);
        partnerContact.setUpdatedBy(sdId);
        return partnerContactRepository.save(partnerContact);
    }

    @Override
    public PartnerContact editPartnerContact(UUID id, Integer sdId, PartnerContactRequest request) {
        PartnerContact partnerContact = getPartnerContact(id);
        partnerContactMapper.updatePartnerContact(request, partnerContact);
        partnerContact.setUpdatedBy(sdId);
        return partnerContactRepository.save(partnerContact);
    }

    @Override
    public void deletePartnerContact(UUID id, Integer sdId) {
        PartnerContact partnerContact = getPartnerContact(id);
        partnerContact.setUpdatedBy(sdId);
        partnerContact.setActive(false);
        partnerContactRepository.save(partnerContact);
    }

    @Override
    public List<PartnerContact> getPartnerContacts(UUID partnerId) {
        partnerService.getPartner(partnerId);
        return partnerContactRepository.findAll(Specification.where(PartnerContactSpecification.isActive(true))
                .and(PartnerContactSpecification.partnerContactByPartnerId(partnerId)));
    }

    private PartnerContact getPartnerContact(UUID id) {
        return partnerContactRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(PartnerContact.class, id));
    }
}