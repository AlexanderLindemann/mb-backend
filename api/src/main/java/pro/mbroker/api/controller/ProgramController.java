package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BankProgramSettingRequest;
import pro.mbroker.api.dto.request.ProgramRequest;

import javax.validation.Valid;
import java.util.UUID;

@Api("API программы кредита")
@RestController
@RequestMapping("/public/program")
public interface ProgramController {
    @ApiOperation("Создать программу кредита")
    @PostMapping()
    ProgramRequest createCreditProgram(@ApiParam(value = "Параметры кредита") @RequestBody BankProgramSettingRequest request);

    @ApiOperation("Получить программу кредита по creditProgramId")
    @GetMapping("/{creditProgramId}")
    ProgramRequest getProgramById(@PathVariable UUID creditProgramId);

    @ApiOperation("обновит программу кредита")
    @PutMapping("/{creditProgramId}")
    ProgramRequest updateProgram(@PathVariable UUID creditProgramId, @RequestBody @Valid BankProgramSettingRequest updateProgramRequest);
}
