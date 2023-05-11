package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CreditProgramController;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.ProgramMapper;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.util.CreditProgramConverter;
import pro.mbroker.app.util.Pagination;

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
    private final CreditProgramConverter creditProgramConverter;

    @Override
    public CreditProgramResponse createCreditProgram(BankProgramRequest request) {
        CreditProgram creditProgram = creditProgramService.createCreditParameter(request, creditProgramConverter.convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramDetailMapper.toProgramDetailResponse(request));
    }

    @Override
    public CreditProgramResponse getProgramByCreditProgramId(UUID creditProgramId) {
        CreditProgram creditProgram = creditProgramService.getProgramByCreditProgramId(creditProgramId);
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    @Override
    public List<CreditProgramResponse> getProgramsByBankId(UUID bankId) {
        List<CreditProgram> programsByBankId = creditProgramService.getProgramsByBankId(bankId);
        return programsByBankId.stream()
                .map(creditProgram -> programMapper.toProgramResponseMapper(creditProgram)
                        .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                .collect(Collectors.toList());
    }

    @Override
    public CreditProgramResponse updateProgram(UUID creditProgramId, BankProgramRequest request) {
        CreditProgram creditProgram = creditProgramService.updateProgram(creditProgramId, request, creditProgramConverter.convertCreditDetailToStringFormat(request));
        return programMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    @Override
    public List<CreditProgramResponse> getAllCreditProgram(int page, int size,
                                                           String sortBy, String sortOrder) {
        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        List<CreditProgram> allCreditProgram = creditProgramService.getAllCreditProgram(pageable);
        List<CreditProgramResponse> creditProgramResponses = allCreditProgram.stream()
                .map(creditProgram -> programMapper.toProgramResponseMapper(creditProgram)
                        .setCreditProgramDetail(creditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                .collect(Collectors.toList());
        return creditProgramResponses;
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deleteCreditProgram(UUID creditProgramId) {
        creditProgramService.deleteCreditProgram(creditProgramId);
    }

}
