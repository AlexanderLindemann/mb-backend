package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;

import javax.validation.Valid;
import java.util.UUID;

@Api("API программы кредита")
@RestController
@RequestMapping("/public/credit_program")
public interface CreditProgramController {
    @ApiOperation("Создать программу кредита")
    @PostMapping()
    CreditProgramResponse createCreditProgram(@ApiParam(value = "Параметры кредита") @RequestBody BankProgramRequest request);

    @ApiOperation("Получить программу кредита по creditProgramId")
    @GetMapping("/{creditProgramId}")
    CreditProgramResponse getProgramById(@PathVariable UUID creditProgramId);

    @ApiOperation("обновить программу кредита")
    @PutMapping("/{creditProgramId}")
    CreditProgramResponse updateProgram(@PathVariable UUID creditProgramId, @RequestBody @Valid BankProgramRequest updateProgramRequest);
}
