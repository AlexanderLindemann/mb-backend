package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.ProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.model.program.CreditParameter;
import pro.mbroker.app.model.program.Program;
import pro.mbroker.app.model.program.ProgramDetail;
import pro.mbroker.app.model.program.ProgramRepository;
import pro.mbroker.app.service.ProgramService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {
    private final CreditParameterMapper creditParameterMapper;
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final ProgramDetailMapper programDetailMapper;


    @Override
    @Transactional
    public Program createCreditParameter(BankProgramRequest createCreditParameter, ProgramDetail programDetail) {
        Program program = programMapper.toProgramMapper(createCreditParameter)
                .setProgramDetail(programDetail)
                .setCreditParameter(creditParameterMapper.toCreditParameterMapper(createCreditParameter.getCreditParameter()));
        return programRepository.save(program);
    }

    @Override
    @Transactional(readOnly = true)
    public Program getProgramById(UUID creditProgramId) {
        return getProgram(creditProgramId);
    }

    @Override
    @Transactional
    public Program updateProgram(UUID creditProgramId, BankProgramRequest updateProgramRequest, ProgramDetail updateProgramDetail) {
        Program program = programRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(Program.class, creditProgramId));
        CreditParameter creditParameter = program.getCreditParameter();
        CreditParameter updateCreditParameter = creditParameterMapper.toCreditParameterMapper(updateProgramRequest.getCreditParameter());
        if (!creditParameter.equals(updateCreditParameter)) {
            creditParameterMapper.updateCreditParameter(updateProgramRequest.getCreditParameter(), creditParameter);
        }
        programMapper.updateProgramFromRequest(updateProgramRequest, program);
        program.setCreditParameter(creditParameter);
        ProgramDetail programDetailCurrent = program.getProgramDetail();
        programDetailMapper.updateProgramDetail(updateProgramDetail, programDetailCurrent);
        return programRepository.save(program);
    }

    private Program getProgram(UUID creditProgramId) {
        return programRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(Program.class, creditProgramId));
    }
}
