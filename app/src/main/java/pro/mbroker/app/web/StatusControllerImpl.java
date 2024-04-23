package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.StatusController;
import pro.mbroker.api.dto.response.PartnerContactResponse;
import pro.mbroker.api.dto.response.SignatureFormStatusResponse;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerContact;
import pro.mbroker.app.mapper.PartnerContactMapper;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.StatusService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusControllerImpl implements StatusController {
    private final StatusService statusService;
    private final BorrowerProfileService borrowerProfileService;
    private final PartnerContactMapper partnerContactMapper;

    @Override
    public SignatureFormStatusResponse isApplicationFullySigned(UUID borrowerId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerId);
        boolean applicationFullySigned =
                statusService.isApplicationFullySigned(borrowerProfile);
        List<PartnerContact> partnerContacts = borrowerProfile.getPartnerApplication().getPartner().getPartnerContacts();
        List<PartnerContactResponse> contactResponses = partnerContacts.stream()
                .map(partnerContactMapper::toPartnerContactResponse)
                .collect(Collectors.toList());
        return new SignatureFormStatusResponse()
                .setApplicationFullySigned(applicationFullySigned)
                .setContacts(contactResponses);
    }
}