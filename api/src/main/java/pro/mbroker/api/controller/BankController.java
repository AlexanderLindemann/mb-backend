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
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.api.dto.response.StorageResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api(value = "API Банка", tags = "API Банка")
@RestController
@RequestMapping("/public/bank")
public interface BankController {
    @ApiOperation("создать банк")
    @PostMapping
    BankResponse createBank(@ApiParam(value = "Параметры банка") @RequestBody BankRequest bankRequest,
                            @RequestParam Integer sdId);

    @ApiOperation("обновить логотип банка")
    @PutMapping("/{bankId}/logo")
    BankResponse updateLogo(@PathVariable UUID bankId,
                            @RequestParam UUID fileStorageId,
                            @RequestParam Integer sdId);

    @ApiOperation("получить все банки")
    @GetMapping()
    List<BankResponse> getAllBank(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "name") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortOrder);

    @ApiOperation("получить банк по id")
    @GetMapping("/{bankId}")
    BankResponse getBankById(@PathVariable UUID bankId);

    @ApiOperation("получить logo по id банка")
    @GetMapping("/{bankId}/logo")
    StorageResponse getLogoBankById(@PathVariable UUID bankId);

    @ApiOperation("обновить банк")
    @PutMapping("/{bankId}")
    BankResponse updateBank(
            @PathVariable UUID bankId,
            @RequestBody @Valid BankRequest bankRequest,
            @RequestParam Integer sdId);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{bankId}")
    void deleteBankById(
            @PathVariable(value = "bankId") UUID bankId,
            @RequestParam Integer sdId);

}
