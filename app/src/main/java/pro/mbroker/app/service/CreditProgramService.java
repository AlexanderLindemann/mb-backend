package pro.mbroker.app.service;

import org.springframework.data.domain.Pageable;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.CreditProgramDetail;

import java.util.List;
import java.util.UUID;

public interface CreditProgramService {

    CreditProgram createCreditParameter(BankProgramRequest createCreditParameter, CreditProgramDetail creditProgramDetail);

    CreditProgram getProgramByCreditProgramId(UUID creditProgramId);

    List<CreditProgram> getProgramByCreditProgramIds(List<UUID> creditProgramId);

    CreditProgram updateProgram(UUID creditProgramId, BankProgramRequest updateProgramRequest, CreditProgramDetail updateCreditProgramDetail);

    List<CreditProgram> getProgramsByBankId(UUID bankId);

    List<CreditProgram> getAllCreditProgram(Pageable pageable);

    void deleteCreditProgram(UUID creditProgramId);

    List<CreditProgram> getProgramsWithDetail(List<UUID> creditProgramIds);

    List<UUID> getAllCreditProgramIds();
}
