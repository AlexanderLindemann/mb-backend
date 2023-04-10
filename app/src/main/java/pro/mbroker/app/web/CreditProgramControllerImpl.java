package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.controller.CreditProgramController;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.mapper.CreditParameterMapper;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.util.CreditProgramConverter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditProgramControllerImpl implements CreditProgramController {
    private final CreditProgramService creditProgramService;
    private final ProgramMapper programMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final CreditParameterMapper creditParameterMapper;
    private final CreditProgramConverter creditProgramConverter;

    @Override
    @Transactional
    public CreditProgramResponse createCreditProgram(BankProgramRequest request) {
        CreditProgram creditProgram = creditProgramService.createCreditParameter(request, creditProgramConverter.convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramDetailMapper.toProgramDetailResponse(request));
    }

    @Override
    public CreditProgramResponse getProgramByCreditProgramId(UUID creditProgramId) {
        CreditProgram creditProgram = creditProgramService.getProgramByCreditProgramId(creditProgramId);
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditParameter(creditParameterMapper
                        .toCreditParameterResponseMapper(creditProgram.getCreditParameter()))
                .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    @Override
    public List<CreditProgramResponse> getProgramsByBankId(UUID bankId) {
        List<CreditProgram> programsByBankId = creditProgramService.getProgramsByBankId(bankId);
        return programsByBankId.stream()
                .map(creditProgram -> programMapper.toProgramResponseMapper(creditProgram)
                        .setCreditParameter(creditParameterMapper
                                .toCreditParameterResponseMapper(creditProgram.getCreditParameter()))
                        .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                .collect(Collectors.toList());
    }

    @Override
    public CreditProgramResponse updateProgram(UUID creditProgramId, BankProgramRequest request) {
        CreditProgram creditProgram = creditProgramService.updateProgram(creditProgramId, request, creditProgramConverter.convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

}
