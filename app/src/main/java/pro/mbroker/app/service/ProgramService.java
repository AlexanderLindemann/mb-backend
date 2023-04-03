package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BankProgramSettingRequest;
import pro.mbroker.api.dto.request.ProgramRequest;

import java.util.UUID;

public interface ProgramService {

    ProgramRequest createCreditParameter(BankProgramSettingRequest createCreditParameter);

    ProgramRequest getProgramById(UUID creditProgramId);

    ProgramRequest updateProgram(UUID creditProgramId, BankProgramSettingRequest updateProgramRequest);
}
