package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.app.model.program.Program;
import pro.mbroker.app.model.program.ProgramDetail;

import java.util.UUID;

public interface ProgramService {

    Program createCreditParameter(BankProgramRequest createCreditParameter, ProgramDetail programDetail);

    Program getProgramById(UUID creditProgramId);

    Program updateProgram(UUID creditProgramId, BankProgramRequest updateProgramRequest, ProgramDetail updateProgramDetail);
}
