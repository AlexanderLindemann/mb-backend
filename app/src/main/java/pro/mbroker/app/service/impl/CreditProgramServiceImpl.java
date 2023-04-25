package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.entity.CreditParameter;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.CreditProgramDetail;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.repository.CreditProgramRepository;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.CreditProgramService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditProgramServiceImpl implements CreditProgramService {
    private final CreditParameterMapper creditParameterMapper;
    private final CreditProgramRepository creditProgramRepository;
    private final ProgramMapper programMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final BankService bankService;


    @Override
    @Transactional
    public CreditProgram createCreditParameter(BankProgramRequest createCreditParameter, CreditProgramDetail creditProgramDetail) {
        CreditProgram creditProgram = programMapper.toProgramMapper(createCreditParameter)
                .setBank(bankService.getBankById(createCreditParameter.getBankId()))
                .setCreditProgramDetail(creditProgramDetail)
                .setCreditParameter(creditParameterMapper.toCreditParameterMapper(createCreditParameter.getCreditParameter()));
        return creditProgramRepository.save(creditProgram);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditProgram getProgramByCreditProgramId(UUID creditProgramId) {
        return getProgram(creditProgramId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditProgram> getProgramByCreditProgramIds(List<UUID> creditProgramIds) {
        return getPrograms(creditProgramIds);
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

    @Override
    @Transactional(readOnly = true)
    public List<CreditProgram> getProgramsByBankId(UUID bankId) {
        return creditProgramRepository.findAllByBankId(bankId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditProgram> getAllCreditProgram(Pageable pageable) {
        return creditProgramRepository.findAllWithBankBy(pageable);
    }

    private CreditProgram getProgram(UUID creditProgramId) {
        return creditProgramRepository.findById(creditProgramId)
                .orElseThrow(() -> new ItemNotFoundException(CreditProgram.class, creditProgramId));
    }

    private List<CreditProgram> getPrograms(List<UUID> creditProgramIds) {
        return creditProgramRepository.findAllByIdIn(creditProgramIds);
    }
}
