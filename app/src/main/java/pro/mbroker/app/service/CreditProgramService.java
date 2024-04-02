package pro.mbroker.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.CreditProgramDetail;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CreditProgramService {

    CreditProgram createCreditParameter(BankProgramRequest createCreditParameter,
                                        CreditProgramDetail creditProgramDetail,
                                        Integer sdId);

    CreditProgram getProgramByCreditProgramId(UUID creditProgramId);

    Set<CreditProgram> getProgramByCreditProgramIds(Set<UUID> creditProgramId);

    CreditProgram updateProgram(UUID creditProgramId,
                                BankProgramRequest updateProgramRequest,
                                CreditProgramDetail updateCreditProgramDetail,
                                Integer sdId);

    List<CreditProgram> getProgramsByBankId(UUID bankId);

    Page<CreditProgram> getAllCreditProgram(Pageable pageable, Specification specification);

    void deleteCreditProgram(UUID creditProgramId, Integer sdId);

    List<CreditProgram> getProgramsWithDetail(List<UUID> creditProgramIds);

    Set<UUID> getAllCreditProgramIds();

    Integer loadCreditProgramFromCian();

    Integer loadBankFutureRulesFromCian();

    Integer loadAdditionalRateRulesFromCian();

    CreditProgram updateProgramFromCian(Integer cianId, BankProgramRequest updateProgramRequest, CreditProgramDetail updateCreditProgramDetail);

    String loadAllFilesFromCian(Boolean makeInactive);
}
