package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerContactController;
import pro.mbroker.api.dto.request.PartnerContactRequest;
import pro.mbroker.api.dto.response.PartnerContactResponse;
import pro.mbroker.app.entity.PartnerContact;
import pro.mbroker.app.mapper.PartnerContactMapper;
import pro.mbroker.app.service.PartnerContactService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerContactControllerImpl implements PartnerContactController {

    private final PartnerContactService partnerContactService;
    private final PartnerContactMapper partnerContactMapper;

    @Override
    public void deletePartnerContact(UUID id, Integer sdId) {
        partnerContactService.deletePartnerContact(id, sdId);
    }

    @Override
    public List<PartnerContactResponse> getPartnerContacts(UUID partnerId) {
        List<PartnerContact> partnerContacts = partnerContactService.getPartnerContacts(partnerId);
        return partnerContacts.stream().map(partnerContactMapper::toPartnerContactResponse).collect(Collectors.toList());
    }

    @Override
    public PartnerContactResponse addPartnerContact(UUID partnerId, Integer sdId, PartnerContactRequest request) {
        PartnerContact partnerContacts = partnerContactService.addPartnerContact(partnerId, sdId, request);
        return partnerContactMapper.toPartnerContactResponse(partnerContacts);
    }

    @Override
    public PartnerContactResponse editPartnerContact(UUID id, Integer sdId, PartnerContactRequest request) {
        PartnerContact partnerContacts = partnerContactService.editPartnerContact(id, sdId, request);
        return partnerContactMapper.toPartnerContactResponse(partnerContacts);
    }
}