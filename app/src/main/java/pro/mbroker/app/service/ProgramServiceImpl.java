package pro.mbroker.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankProgramSettingRequest;
import pro.mbroker.api.dto.request.ProgramDetailRequest;
import pro.mbroker.api.dto.request.ProgramRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.ProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.model.program.CreditParameter;
import pro.mbroker.app.model.program.Program;
import pro.mbroker.app.model.program.ProgramDetail;
import pro.mbroker.app.model.program.ProgramRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {
    private final CreditParameterMapper creditParameterMapper;
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final ProgramDetailMapper programDetailMapper;


    @Override
    public ProgramRequest createCreditParameter(BankProgramSettingRequest createCreditParameter) {
        convertCreditDetailToStringFormat(createCreditParameter);

        Program program = programMapper.toProgramMapper(createCreditParameter)
                .setProgramDetail(convertCreditDetailToStringFormat(createCreditParameter))
                .setCreditParameter(creditParameterMapper.toCreditParameterMapper(createCreditParameter.getCreditParameter()));

        return programMapper.toProgramRequestMapper(programRepository.save(program))
                .setProgramDetail(new ProgramDetailRequest()
                        .setCreditPurposeType(createCreditParameter.getCreditPurposeType())
                        .setRealEstateType(createCreditParameter.getRealEstateType())
                        .setInclude(createCreditParameter.getInclude())
                        .setExclude(createCreditParameter.getExclude())
                );
    }

    private ProgramDetailRequest convertCreditDetailToEnumFormat(ProgramDetail programDetail) {
        return new ProgramDetailRequest()
                .setCreditPurposeType(convertStringListToEnumList(programDetail.getCreditPurposeType(), CreditPurposeType.class))
                .setRealEstateType(convertStringListToEnumList(programDetail.getRealEstateType(), RealEstateType.class))
                .setInclude(convertStringListToEnumList(programDetail.getInclude(), RegionType.class))
                .setExclude(convertStringListToEnumList(programDetail.getExclude(), RegionType.class));
    }

    private ProgramDetail convertCreditDetailToStringFormat(BankProgramSettingRequest createCreditParameter) {
        return new ProgramDetail()
                .setCreditPurposeType(convertEnumListToStringList(createCreditParameter.getCreditPurposeType()))
                .setInclude(convertEnumListToStringList(createCreditParameter.getInclude()))
                .setExclude(convertEnumListToStringList(createCreditParameter.getExclude()))
                .setRealEstateType(convertEnumListToStringList(createCreditParameter.getRealEstateType()));
    }

    private String convertEnumListToStringList(List<? extends Enum<?>> enums) {
        return enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    private <T extends Enum<T>> List<T> convertStringListToEnumList(String str, Class<T> enumClass) {
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .map(strEnum -> Enum.valueOf(enumClass, strEnum))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProgramRequest getProgramById(UUID creditProgramId) {
        Program program = getProgram(creditProgramId);
        return programMapper.toProgramRequestMapper(program)
                .setCreditParameter(creditParameterMapper
                        .toCreditParameterRequestMapper(program.getCreditParameter()))
                .setProgramDetail(convertCreditDetailToEnumFormat(program.getProgramDetail()));
    }

    @Override
    public ProgramRequest updateProgram(UUID creditProgramId, BankProgramSettingRequest updateProgramRequest) {
        Program program = programRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(Program.class, creditProgramId));
        CreditParameter creditParameter = program.getCreditParameter();
        CreditParameter updateCreditParameter = creditParameterMapper.toCreditParameterMapper(updateProgramRequest.getCreditParameter());
        if (!creditParameter.equals(updateCreditParameter)) {
            creditParameterMapper.updateCreditParameter(updateProgramRequest.getCreditParameter(), creditParameter);
        }
        programMapper.updateProgramFromRequest(updateProgramRequest, program);
        program.setCreditParameter(creditParameter);
        ProgramDetail programDetail = program.getProgramDetail();
        ProgramDetail updateProgramDetail = convertCreditDetailToStringFormat(updateProgramRequest);
        programDetailMapper.updateProgramDetail(updateProgramDetail, programDetail);

        programRepository.save(program);

        return programMapper.toProgramRequestMapper(program)
                .setProgramDetail(convertCreditDetailToEnumFormat(program.getProgramDetail()));
    }

    private Program getProgram(UUID creditProgramId) {
        return programRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(Program.class, creditProgramId));
    }
}
