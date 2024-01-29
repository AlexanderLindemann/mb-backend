package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerRealEstateController;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.dto.response.RealEstateResponse;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.mapper.CreditProgramMapper;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.service.PartnerRealEstateService;
import pro.mbroker.app.util.Pagination;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerRealEstateControllerImpl implements PartnerRealEstateController {
    private final PartnerRealEstateService partnerRealEstateService;
    private final PartnerMapper partnerMapper;
    private final CreditProgramMapper creditProgramMapper;
    private final RealEstateMapper realEstateMapper;

    @Override
    public PartnerResponse addRealEstate(UUID partnerId, RealEstateRequest request, Integer sdId) {
        RealEstate realEstate = partnerRealEstateService.addRealEstate(partnerId, request, sdId);
        return buildPartnerResponse(realEstate.getPartner());
    }

    @Override
    public void deleteRealEstate(UUID realEstateId, Integer sdId) {
        partnerRealEstateService.deleteRealEstate(realEstateId, sdId);
    }

    @Override
    public PartnerResponse updateRealEstate(RealEstateRequest request, Integer sdId) {
        RealEstate realEstate = partnerRealEstateService.updateRealEstate(request.getId(), request, sdId);
        return buildPartnerResponse(realEstate.getPartner());
    }

    @Override
    public List<RealEstateResponse> getRealEstateByPartnerId(int page, int size, String sortBy, String sortOrder, UUID partnerId) {
        List<RealEstate> realEstates = partnerRealEstateService.getRealEstateByPartnerId(page, size, sortBy, sortOrder, partnerId);
        return realEstateMapper.toRealEstateAddressResponseList(realEstates);
    }

    @Override
    public List<RealEstateResponse> getCurrentRealEstate(int page, int size, String sortBy, String sortOrder, Integer organisationId) {
        List<RealEstate> currentRealEstates = partnerRealEstateService.getCurrentRealEstate(page, size, sortBy, sortOrder, organisationId);
        return realEstateMapper.toRealEstateAddressResponseList(currentRealEstates);
    }

    @Override
    public void loadRealEstatesFromCian() {
        partnerRealEstateService.loadRealEstatesFromCian();
    }

    @Override
    public ResponseEntity<Page<RealEstateResponse>> findRealEstatesByName(int page,
                                                                          int size,
                                                                          String sortBy,
                                                                          String sortOrder,
                                                                          String realEstateName) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy).and(Sort.by("id"));
        Page<RealEstate> realEstatesPage =
                partnerRealEstateService.findRealEstatesByName(Pagination.createPageable(page, size, sort), realEstateName);
        Page<RealEstateResponse> responsePage =
                realEstatesPage.map(realEstateMapper::toRealEstateResponseMapper);
        return ResponseEntity.ok(responsePage);
    }

    private PartnerResponse buildPartnerResponse(Partner partner) {
        List<CreditProgramResponse> creditProgramResponses = creditProgramMapper.convertCreditProgramsToResponses(partner.getCreditPrograms());
        List<RealEstateResponse> realEstateResponse = partner.getRealEstates().stream()
                .map(realEstateMapper::toRealEstateResponseMapper)
                .collect(Collectors.toList());
        return partnerMapper.toPartnerResponseMapper(partner)
                .setBankCreditProgram(creditProgramResponses)
                .setRealEstates(realEstateResponse);
    }
}
