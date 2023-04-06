package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api("API программы кредита")
@RestController
@RequestMapping("/public/credit_program")
public interface CreditProgramController {
    @ApiOperation("Создать программу кредита")
    @PostMapping()
    CreditProgramResponse createCreditProgram(@ApiParam(value = "Параметры кредита") @RequestBody BankProgramRequest request);

    @ApiOperation("Получить программу кредита по идентификатору")
    @GetMapping("/credit-programs/{creditProgramId}")
    CreditProgramResponse getProgramByCreditProgramId(@PathVariable UUID creditProgramId);

    @ApiOperation("Получить список программ кредитования по идентификатору банка")
    @GetMapping("/banks/{bankId}/credit-programs")
    List<CreditProgramResponse> getProgramsByBankId(@PathVariable UUID bankId);

    @ApiOperation("обновить программу кредита")
    @PutMapping("/{creditProgramId}")
    CreditProgramResponse updateProgram(@PathVariable UUID creditProgramId, @RequestBody @Valid BankProgramRequest updateProgramRequest);
}
