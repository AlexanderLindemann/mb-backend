package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CreditProgramController;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.request.CreditProgramServiceRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.mapper.CreditProgramDetailMapper;
import pro.mbroker.app.mapper.CreditProgramMapper;
import pro.mbroker.app.repository.specification.CreditProgramSpecification;
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
    private final CreditProgramMapper creditProgramMapper;
    private final CreditProgramDetailMapper creditProgramDetailMapper;
    private final CreditProgramConverter creditProgramConverter;

    @Override
    public CreditProgramResponse createCreditProgram(BankProgramRequest request, Integer sdId) {
        CreditProgram creditProgram = creditProgramService.createCreditParameter(request,
                CreditProgramConverter.convertCreditDetailToStringFormat(request), sdId);
        return creditProgramMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(creditProgramDetailMapper.toProgramDetailResponse(request));
    }

    @Override
    public CreditProgramResponse getProgramByCreditProgramId(UUID creditProgramId) {
        CreditProgram creditProgram = creditProgramService.getProgramByCreditProgramId(creditProgramId);
        return creditProgramMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(CreditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    @Override
    public List<CreditProgramResponse> getProgramsByBankId(UUID bankId) {
        List<CreditProgram> programsByBankId = creditProgramService.getProgramsByBankId(bankId);
        return programsByBankId.stream()
                .map(creditProgram -> creditProgramMapper.toProgramResponseMapper(creditProgram)
                        .setCreditProgramDetail(CreditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                .collect(Collectors.toList());
    }

    @Override
    public CreditProgramResponse updateProgram(UUID creditProgramId,
                                               BankProgramRequest request,
                                               Integer sdId) {
        CreditProgram creditProgram = creditProgramService.updateProgram(creditProgramId,
                request,
                CreditProgramConverter.convertCreditDetailToStringFormat(request),
                sdId);
        return creditProgramMapper.toProgramResponseMapper(creditProgram)
                .setCreditProgramDetail(CreditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
    }

    @Override
    public Page<CreditProgramResponse> getAllCreditProgram(CreditProgramServiceRequest request) {
        Pageable pageable = Pagination.createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortOrder());
        Specification<CreditProgram> specification = CreditProgramSpecification.buildSpecification(request);
        Page<CreditProgram> allCreditProgram = creditProgramService.getAllCreditProgram(pageable, specification);
        return allCreditProgram.map(creditProgram -> {
            CreditProgramResponse response = creditProgramMapper.toProgramResponseMapper(creditProgram);
            response.setCreditProgramDetail(
                    CreditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail()));
            return response;
        });
    }

    @Override
    public void deleteCreditProgram(UUID creditProgramId, Integer sdId) {
        creditProgramService.deleteCreditProgram(creditProgramId, sdId);
    }

    @Override
    public void loadBankFutureRulesFromCian() {
        creditProgramService.loadBankFutureRulesFromCian();
    }

    @Override
    public void loadAdditionalRateRulesFromCian() {
        creditProgramService.loadAdditionalRateRulesFromCian();
    }

    @Override
    public String loadAllFilesFromCian(Boolean makeInactive) {
        return creditProgramService.loadAllFilesFromCian(makeInactive);
    }
}
