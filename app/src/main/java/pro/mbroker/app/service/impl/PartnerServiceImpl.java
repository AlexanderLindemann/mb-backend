package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Converter;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final BankService bankService;
    private final CreditProgramService creditProgramService;
    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;

    @Override
    public Partner createPartner(PartnerRequest request) {
        Partner partner = partnerMapper.toPartnerMapper(request)
                .setBank(bankService.getBankById(request.getBankId()))
                .setRealEstateType(Converter.convertEnumListToStringList(request.getRealEstateTypes()))
                .setCreditPurposeType(Converter.convertEnumListToStringList(request.getCreditPurposeType()))
                .setCreditPrograms(creditProgramService.getProgramByCreditProgramIds(request.getBankCreditProgram()));

        return partnerRepository.save(partner);
    }
}
