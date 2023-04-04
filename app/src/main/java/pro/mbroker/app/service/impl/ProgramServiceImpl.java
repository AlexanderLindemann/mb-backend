package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.model.program.CreditParameter;
import pro.mbroker.app.model.program.CreditProgram;
import pro.mbroker.app.model.program.CreditProgramDetail;
import pro.mbroker.app.model.program.CreditProgramRepository;
import pro.mbroker.app.service.ProgramService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {
    private final CreditParameterMapper creditParameterMapper;
    private final CreditProgramRepository creditProgramRepository;
    private final ProgramMapper programMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;


    @Override
    @Transactional
    public CreditProgram createCreditParameter(BankProgramRequest createCreditParameter, CreditProgramDetail creditProgramDetail) {
        CreditProgram creditProgram = programMapper.toProgramMapper(createCreditParameter)
                .setCreditProgramDetail(creditProgramDetail)
                .setCreditParameter(creditParameterMapper.toCreditParameterMapper(createCreditParameter.getCreditParameter()));
        return creditProgramRepository.save(creditProgram);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditProgram getProgramById(UUID creditProgramId) {
        return getProgram(creditProgramId);
    }

    @Override
    @Transactional
    public CreditProgram updateProgram(UUID creditProgramId, BankProgramRequest updateProgramRequest, CreditProgramDetail updateCreditProgramDetail) {
        CreditProgram creditProgram = creditProgramRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
        CreditParameter creditParameter = creditProgram.getCreditParameter();
        CreditParameter updateCreditParameter = creditParameterMapper.toCreditParameterMapper(updateProgramRequest.getCreditParameter());
        if (!creditParameter.equals(updateCreditParameter)) {
            creditParameterMapper.updateCreditParameter(updateProgramRequest.getCreditParameter(), creditParameter);
        }
        programMapper.updateProgramFromRequest(updateProgramRequest, creditProgram);
        creditProgram.setCreditParameter(creditParameter);
        CreditProgramDetail creditProgramDetailCurrent = creditProgram.getCreditProgramDetail();
        creditProgramDetailMapper.updateProgramDetail(updateCreditProgramDetail, creditProgramDetailCurrent);
        return creditProgramRepository.save(creditProgram);
    }

    private CreditProgram getProgram(UUID creditProgramId) {
        return creditProgramRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
    }
}
