package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerRealEstateController;
import pro.mbroker.api.dto.request.RealEstateAddressRequest;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.dto.response.RealEstateAddressResponse;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstateAddress;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.mapper.RealEstateAddressMapper;
import pro.mbroker.app.service.PartnerRealEstateService;
import pro.mbroker.app.service.PartnerService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerRealEstateControllerImpl implements PartnerRealEstateController {
    private final PartnerService partnerService;
    private final PartnerRealEstateService partnerRealEstateService;
    private final PartnerMapper partnerMapper;
    private final BankMapper bankMapper;
    private final ProgramMapper programMapper;
    private final RealEstateAddressMapper realEstateAddressMapper;

    @Override
    public PartnerResponse addRealEstateAddress(UUID partnerId, RealEstateAddressRequest request) {
        partnerRealEstateService.addRealEstateAddress(partnerId, request);
        Partner partner = partnerService.getPartner(partnerId);
        return buildPartnerResponse(partner);
    }

    @Override
    public void deleteRealEstateAddress(UUID addressId) {
        partnerRealEstateService.deleteRealEstateAddress(addressId);
    }

    @Override
    public PartnerResponse updateRealEstateAddress(UUID addressId, RealEstateAddressRequest request) {
        RealEstateAddress realEstateAddress = partnerRealEstateService.updateRealEstateAddress(addressId, request);
        Partner partner = partnerService.getPartner(realEstateAddress.getPartner().getId());
        return buildPartnerResponse(partner);
    }

    @Override
    public List<RealEstateAddressResponse> getRealEstateAddressByPartnerId(int page, int size, String sortBy, String sortOrder, UUID partnerId) {
        List<RealEstateAddress> address = partnerRealEstateService.getRealEstateAddressByPartnerId(page, size, sortBy, sortOrder, partnerId);
        return realEstateAddressMapper.toRealEstateAddressResponseList(address);
    }

    private PartnerResponse buildPartnerResponse(Partner partner) {
        List<CreditProgramResponse> creditProgramResponses = programMapper.convertCreditProgramsToResponses(partner.getCreditPrograms());
        BankResponse bankResponse = bankMapper.toBankResponseMapper(partner.getBank());
        List<RealEstateAddressResponse> realEstateAddressResponses = partner.getRealEstateAddress().stream()
                .map(realEstateAddressMapper::toRealEstateAddressResponseMapper)
                .collect(Collectors.toList());
        return partnerMapper.toPartnerResponseMapper(partner)
                .setBankCreditProgram(creditProgramResponses)
                .setRealEstateAddress(realEstateAddressResponses)
                .setBank(bankResponse);
    }
}
