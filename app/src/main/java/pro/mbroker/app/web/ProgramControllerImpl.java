package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.ProgramController;
import pro.mbroker.api.dto.request.BankProgramSettingRequest;
import pro.mbroker.api.dto.request.ProgramRequest;
import pro.mbroker.app.service.ProgramService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramControllerImpl implements ProgramController {
    private final ProgramService programService;

    @Override
    public ProgramRequest createCreditProgram(BankProgramSettingRequest settingRequest) {
        return programService.createCreditParameter(settingRequest);
    }

    @Override
    public ProgramRequest getProgramById(UUID creditProgramId) {
        return programService.getProgramById(creditProgramId);
    }

    @Override
    public ProgramRequest updateProgram(UUID creditProgramId, BankProgramSettingRequest updateProgramRequest) {
        return programService.updateProgram(creditProgramId, updateProgramRequest);
    }
}
