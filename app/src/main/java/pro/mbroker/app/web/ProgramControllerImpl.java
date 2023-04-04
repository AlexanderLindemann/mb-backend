package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.ProgramController;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.ProgramDetailResponse;
import pro.mbroker.api.dto.response.ProgramResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.model.program.Program;
import pro.mbroker.app.model.program.ProgramDetail;
import pro.mbroker.app.service.ProgramService;
import pro.mbroker.app.util.Converter;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramControllerImpl implements ProgramController {
    private final ProgramService programService;
    private final ProgramMapper programMapper;
    private final CreditParameterMapper creditParameterMapper;

    @Override
    public ProgramResponse createCreditProgram(BankProgramRequest request) {
        Program program = programService.createCreditParameter(request, convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(program)
                .setProgramDetail(new ProgramDetailResponse()
                        .setCreditPurposeType(request.getCreditPurposeType())
                        .setRealEstateType(request.getRealEstateType())
                        .setInclude(request.getInclude())
                        .setExclude(request.getExclude())
                );
    }

    @Override
    public ProgramResponse getProgramById(UUID creditProgramId) {
        Program program = programService.getProgramById(creditProgramId);
        return programMapper.toProgramResponseMapper(program)
                .setCreditParameter(creditParameterMapper
                        .toCreditParameterResponseMapper(program.getCreditParameter()))
                .setProgramDetail(convertCreditDetailToEnumFormat(program.getProgramDetail()));
    }

    @Override
    public ProgramResponse updateProgram(UUID creditProgramId, BankProgramRequest request) {
        Program program = programService.updateProgram(creditProgramId, request, convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(program)
                .setProgramDetail(convertCreditDetailToEnumFormat(program.getProgramDetail()));
    }

    private ProgramDetailResponse convertCreditDetailToEnumFormat(ProgramDetail programDetail) {
        return new ProgramDetailResponse()
                .setCreditPurposeType(Converter.convertStringListToEnumList(programDetail.getCreditPurposeType(), CreditPurposeType.class))
                .setRealEstateType(Converter.convertStringListToEnumList(programDetail.getRealEstateType(), RealEstateType.class))
                .setInclude(Converter.convertStringListToEnumList(programDetail.getInclude(), RegionType.class))
                .setExclude(Converter.convertStringListToEnumList(programDetail.getExclude(), RegionType.class));
    }

    private ProgramDetail convertCreditDetailToStringFormat(BankProgramRequest createCreditParameter) {
        return new ProgramDetail()
                .setCreditPurposeType(Converter.convertEnumListToStringList(createCreditParameter.getCreditPurposeType()))
                .setInclude(Converter.convertEnumListToStringList(createCreditParameter.getInclude()))
                .setExclude(Converter.convertEnumListToStringList(createCreditParameter.getExclude()))
                .setRealEstateType(Converter.convertEnumListToStringList(createCreditParameter.getRealEstateType()));
    }
}
