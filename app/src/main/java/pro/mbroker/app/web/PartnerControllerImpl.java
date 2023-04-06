package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerController;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.PartnerMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.Converter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerControllerImpl implements PartnerController {
    private final PartnerService partnerService;
    private final PartnerMapper partnerMapper;
    private final ProgramMapper programMapper;
    private final BankMapper bankMapper;
    private final CreditParameterMapper creditParameterMapper;


    @Override
    public PartnerResponse createPartner(PartnerRequest request) {
        Partner partner = partnerService.createPartner(request);
        List<CreditProgramResponse> creditProgramResponses = convertCreditProgramsToResponses(partner.getCreditPrograms());
        BankResponse bankResponse = convertBankToResponse(partner.getBank());
        return partnerMapper.toPartnerResponseMapper(partner)
                .setBankCreditProgram(creditProgramResponses)
                .setRealEstateType(Converter.convertStringListToEnumList(partner.getRealEstateType(), RealEstateType.class))
                .setCreditPurposeType(Converter.convertStringListToEnumList(partner.getCreditPurposeType(), CreditPurposeType.class))
                .setBank(bankResponse);
    }

    private List<CreditProgramResponse> convertCreditProgramsToResponses(List<CreditProgram> creditPrograms) {
        return creditPrograms.stream()
                .map(this::convertCreditProgramToResponse)
                .collect(Collectors.toList());
    }

    private CreditProgramResponse convertCreditProgramToResponse(CreditProgram creditProgram) {
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditParameter(creditParameterMapper.toCreditParameterResponseMapper(creditProgram.getCreditParameter()))
                .setCreditProgramDetail(Converter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    private BankResponse convertBankToResponse(Bank bank) {
        List<CreditProgramResponse> creditProgramResponses = convertCreditProgramsToResponses(bank.getCreditPrograms());
        return bankMapper.toBankResponseMapper(bank)
                .setCreditProgram(creditProgramResponses);
    }
}
