package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerRealEstateController;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.dto.response.RealEstateResponse;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
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
    private final ProgramMapper programMapper;
    private final RealEstateMapper realEstateMapper;

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public PartnerResponse addRealEstate(UUID partnerId, RealEstateRequest request) {
        partnerRealEstateService.addRealEstate(partnerId, request);
        Partner partner = partnerService.getPartner(partnerId);
        return buildPartnerResponse(partner);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deleteRealEstate(UUID realEstateId) {
        partnerRealEstateService.deleteRealEstate(realEstateId);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or" +
            " hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public PartnerResponse updateRealEstate(RealEstateRequest request) {
        RealEstate realEstate = partnerRealEstateService.updateRealEstate(request.getId(), request);
        Partner partner = partnerService.getPartner(realEstate.getPartner().getId());
        return buildPartnerResponse(partner);
    }

    @Override
    public List<RealEstateResponse> getRealEstateByPartnerId(int page, int size, String sortBy, String sortOrder, UUID partnerId) {
        List<RealEstate> realEstates = partnerRealEstateService.getRealEstateByPartnerId(page, size, sortBy, sortOrder, partnerId);
        return realEstateMapper.toRealEstateAddressResponseList(realEstates);
    }

    @Override
    public List<RealEstateResponse> getCurrentRealEstate(int page, int size, String sortBy, String sortOrder) {
        List<RealEstate> currentRealEstates = partnerRealEstateService.getCurrentRealEstate(page, size, sortBy, sortOrder);
        return realEstateMapper.toRealEstateAddressResponseList(currentRealEstates);
    }

    private PartnerResponse buildPartnerResponse(Partner partner) {
        List<CreditProgramResponse> creditProgramResponses = programMapper.convertCreditProgramsToResponses(partner.getCreditPrograms());
        List<RealEstateResponse> realEstateResponse = partner.getRealEstates().stream()
                .map(realEstateMapper::toRealEstateResponseMapper)
                .collect(Collectors.toList());
        return partnerMapper.toPartnerResponseMapper(partner)
                .setBankCreditProgram(creditProgramResponses)
                .setRealEstates(realEstateResponse);
    }
}
