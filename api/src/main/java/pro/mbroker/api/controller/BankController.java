package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.response.BankResponse;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Api(value = "API Банка", tags = "API Банка")
@RestController
@RequestMapping("/public/bank")
public interface BankController {
    @ApiOperation("создать банк")
    @PostMapping
    BankResponse createBank(
            @RequestParam("name") String name
    );

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
    MultipartFile getLogoBankById(@PathVariable UUID bankId);

    @ApiOperation("обновить name банка по id банка")
    @PutMapping("/{bankId}")
    BankResponse updateBankName(
            @PathVariable UUID bankId,
            @NotBlank @RequestParam("name") String name);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{bankId}")
    void deleteBankById(
            @PathVariable(value = "bankId") UUID bankId
    );

}
