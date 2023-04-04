package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.model.program.CreditProgram;
import pro.mbroker.app.model.program.CreditProgramDetail;

import java.util.UUID;

public interface ProgramService {

    CreditProgram createCreditParameter(BankProgramRequest createCreditParameter, CreditProgramDetail creditProgramDetail);

    CreditProgram getProgramById(UUID creditProgramId);

    CreditProgram updateProgram(UUID creditProgramId, BankProgramRequest updateProgramRequest, CreditProgramDetail updateCreditProgramDetail);
}
