package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.controller.PartnerController;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.mapper.RealEstateMapper;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.CreditProgramConverter;

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
    private final ProgramMapper programMapper;
    private final CreditProgramConverter creditProgramConverter;


    @Override
    public PartnerResponse createPartner(PartnerRequest request) {
        Partner partner = partnerService.createPartner(request);
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
    public PartnerResponse getPartnerById(UUID partnerId) {
        Partner partner = partnerService.getPartner(partnerId);
        return buildPartnerResponse(partner);
    }

    private PartnerResponse buildPartnerResponse(Partner partner) {
        List<CreditProgramResponse> creditProgramResponses = partner.getCreditPrograms().stream()
                .map(creditProgram -> programMapper.toProgramResponseMapper(creditProgram)
                        .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                .collect(Collectors.toList());


        return partnerMapper.toPartnerResponseMapper(partner)
                .setBankCreditProgram(creditProgramResponses)
                .setRealEstates(realEstateMapper.toRealEstateAddressResponseList(partner.getRealEstates()));
    }

}
