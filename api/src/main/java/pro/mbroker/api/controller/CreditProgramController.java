package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.BankProgramRequest;
import pro.mbroker.api.dto.response.CreditProgramResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api(value = "API Программы кредита", tags = "API Программы кредита")
@RestController
@RequestMapping("/public/credit_program")
public interface CreditProgramController {
    @ApiOperation("Создать программу кредита")
    @PostMapping()
    CreditProgramResponse createCreditProgram(@ApiParam(value = "Параметры кредита")
                                              @RequestBody BankProgramRequest request, Integer sdId);

    @ApiOperation("Получить программу кредита по идентификатору")
    @GetMapping("/{creditProgramId}")
    CreditProgramResponse getProgramByCreditProgramId(@PathVariable UUID creditProgramId);

    @ApiOperation("Получить список программ кредитования по идентификатору банка")
    @GetMapping("/bank/{bankId}")
    List<CreditProgramResponse> getProgramsByBankId(@PathVariable UUID bankId);

    @ApiOperation("обновить программу кредита")
    @PutMapping("/{creditProgramId}")
    CreditProgramResponse updateProgram(@PathVariable UUID creditProgramId,
                                        @RequestBody @Valid BankProgramRequest updateProgramRequest,
                                        Integer sdId);

    @ApiOperation("Получить список всех кредитных программ")
    @GetMapping()
    List<CreditProgramResponse> getAllCreditProgram(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String sortOrder);

    @ApiOperation("удалить программу кредита по id")
    @DeleteMapping("/{creditProgramId}")
    void deleteCreditProgram(@PathVariable(value = "creditProgramId") UUID creditProgramId,
                             Integer sdId);

    @ApiOperation("Получить bank future rules из циан")
    @GetMapping("/load/future_rules_from_cian")
    void loadBankFutureRulesFromCian();


    @ApiOperation("Получить additional rate rules из циан")
    @GetMapping("/load/AdditionalRateRulesFromCian")
    void loadAdditionalRateRulesFromCian();

    @ApiOperation("Запустить выгрузку из всех файлов по кредитным программам из циан")
    @PostMapping("/load/LoadAllFilesFromCian")
    String loadAllFilesFromCian(Boolean makeInactive );

}
