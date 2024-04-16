package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.controller.PartnerController;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.dto.response.RealEstateResponse;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerContact;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.mapper.CreditProgramMapper;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.CreditProgramConverter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerControllerImpl implements PartnerController {
    private final PartnerService partnerService;
    private final PartnerMapper partnerMapper;
    private final RealEstateMapper realEstateMapper;
    private final CreditProgramMapper creditProgramMapper;

    @Override
    public PartnerResponse createPartner(PartnerRequest request, Integer sdId) {
        Partner partner = partnerService.createPartner(request, sdId);
        return buildPartnerResponse(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerResponse> getAllPartner(int page, int size, String sortBy, String sortOrder) {
        List<Partner> partnerList = partnerService.getAllPartner(page, size, sortBy, sortOrder);
        return partnerList.stream()
                .map(this::buildPartnerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PartnerResponse getPartnerResponseById(UUID partnerId) {
        Partner partner = partnerService.getIsActivePartner(partnerId);
        return buildPartnerResponse(partner);
    }

    @Override
    public PartnerResponse updatePartnerById(UUID partnerId, PartnerRequest request, Integer sdId) {
        Partner partner = partnerService.updatePartnerById(partnerId, request, sdId);
        return buildPartnerResponse(partner);
    }

    @Override
    public List<PartnerResponse> getCurrentPartner(Integer organisationId) {
        List<Partner> partner = partnerService.getCurrentPartners(organisationId);
        return partner.stream()
                .map(this::buildPartnerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PartnerResponse> getPartnersByPartnerApplicationId(UUID partnerApplicationId) {
        Partner partner = partnerService.getPartnerByPartnerApplicationId(partnerApplicationId);
        List<Partner> partners =
                partnerService.getPartnersBySmartDealOrganizationId(partner.getSmartDealOrganizationId());
        return partners.stream().map(this::buildPartnerResponse).collect(Collectors.toList());
    }

    @Override
    public void deletePartner(UUID partnerId, Integer sdId) {
        partnerService.deletePartner(partnerId, sdId);
    }

    private PartnerResponse buildPartnerResponse(Partner partner) {
        List<RealEstate> activeRealEstates = partner.getRealEstates().stream()
                .filter(RealEstate::isActive)
                .collect(Collectors.toList());
        partner.setPartnerContacts(partner.getPartnerContacts().stream()
                .filter(PartnerContact::isActive)
                .collect(Collectors.toList()));
        List<CreditProgramResponse> activeCreditProgram = partner.getCreditPrograms().stream()
                .filter(cp -> cp.isActive() && cp.getBank().isActive())
                .map(creditProgram -> creditProgramMapper.toProgramResponseMapper(creditProgram)
                        .setCreditProgramDetail(CreditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                .collect(Collectors.toList());
        List<RealEstateResponse> realEstateResponses = realEstateMapper.toRealEstateAddressResponseList(activeRealEstates);
        realEstateResponses.sort(Comparator.comparing(RealEstateResponse::getResidentialComplexName));
        return partnerMapper.toPartnerResponseMapper(partner)
                .setBankCreditProgram(activeCreditProgram)
                .setRealEstates(realEstateResponses);
    }
}