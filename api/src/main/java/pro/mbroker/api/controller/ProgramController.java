package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.ProgramResponse;

import javax.validation.Valid;
import java.util.UUID;

@Api("API программы кредита")
@RestController
@RequestMapping("/public/program")
public interface ProgramController {
    @ApiOperation("Создать программу кредита")
    @PostMapping()
    ProgramResponse createCreditProgram(@ApiParam(value = "Параметры кредита") @RequestBody BankProgramRequest request);

    @ApiOperation("Получить программу кредита по creditProgramId")
    @GetMapping("/{creditProgramId}")
    ProgramResponse getProgramById(@PathVariable UUID creditProgramId);

    @ApiOperation("обновит программу кредита")
    @PutMapping("/{creditProgramId}")
    ProgramResponse updateProgram(@PathVariable UUID creditProgramId, @RequestBody @Valid BankProgramRequest updateProgramRequest);
}
