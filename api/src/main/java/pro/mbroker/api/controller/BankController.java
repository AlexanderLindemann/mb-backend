package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.response.AttachmentResponse;
import pro.mbroker.api.dto.response.BankResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api(value = "API Банка", tags = "API Банка")
@RestController
@RequestMapping("/public/bank")
public interface BankController {
    @ApiOperation("создать банк")
    @PostMapping
    BankResponse createBank
            (@ApiParam(value = "Параметры банка") @RequestBody BankRequest bankRequest);

    @ApiOperation("обновить логотип банка")
    @PutMapping("/{bankId}/logo")
    BankResponse updateLogo(@PathVariable UUID bankId,
                            @RequestParam("logo") MultipartFile logo
    );

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
    AttachmentResponse getLogoBankById(@PathVariable UUID bankId);

    @ApiOperation("обновить банк")
    @PutMapping("/{bankId}")
    BankResponse updateBank(
            @PathVariable UUID bankId,
            @RequestBody @Valid BankRequest bankRequest);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{bankId}")
    void deleteBankById(
            @PathVariable(value = "bankId") UUID bankId
    );

}
