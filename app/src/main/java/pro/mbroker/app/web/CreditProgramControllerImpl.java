package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CreditProgramController;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditProgramDetailResponse;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.model.program.CreditProgram;
import pro.mbroker.app.model.program.CreditProgramDetail;
import pro.mbroker.app.service.ProgramService;
import pro.mbroker.app.util.Converter;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditProgramControllerImpl implements CreditProgramController {
    private final ProgramService programService;
    private final ProgramMapper programMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final CreditParameterMapper creditParameterMapper;

    @Override
    public CreditProgramResponse createCreditProgram(BankProgramRequest request) {
        CreditProgram creditProgram = programService.createCreditParameter(request, convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramDetailMapper.toProgramDetailResponse(request));
    }

    @Override
    public CreditProgramResponse getProgramById(UUID creditProgramId) {
        CreditProgram creditProgram = programService.getProgramById(creditProgramId);
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditParameter(creditParameterMapper
                        .toCreditParameterResponseMapper(creditProgram.getCreditParameter()))
                .setCreditProgramDetail(convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    @Override
    public CreditProgramResponse updateProgram(UUID creditProgramId, BankProgramRequest request) {
        CreditProgram creditProgram = programService.updateProgram(creditProgramId, request, convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    private CreditProgramDetailResponse convertCreditDetailToEnumFormat(CreditProgramDetail creditProgramDetail) {
        return new CreditProgramDetailResponse()
                .setCreditPurposeType(Converter.convertStringListToEnumList(creditProgramDetail.getCreditPurposeType(), CreditPurposeType.class))
                .setRealEstateType(Converter.convertStringListToEnumList(creditProgramDetail.getRealEstateType(), RealEstateType.class))
                .setInclude(Converter.convertStringListToEnumList(creditProgramDetail.getInclude(), RegionType.class))
                .setExclude(Converter.convertStringListToEnumList(creditProgramDetail.getExclude(), RegionType.class));
    }

    private CreditProgramDetail convertCreditDetailToStringFormat(BankProgramRequest createCreditParameter) {
        return new CreditProgramDetail()
                .setCreditPurposeType(Converter.convertEnumListToStringList(createCreditParameter.getCreditPurposeType()))
                .setInclude(Converter.convertEnumListToStringList(createCreditParameter.getInclude()))
                .setExclude(Converter.convertEnumListToStringList(createCreditParameter.getExclude()))
                .setRealEstateType(Converter.convertEnumListToStringList(createCreditParameter.getRealEstateType()));
    }
}
