package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.response.BankResponse;

import java.util.List;
import java.util.UUID;

@Api("API Банка")
@RestController
@RequestMapping("/public/bank")
public interface BankController {
    @ApiOperation("создать банк")
    @PostMapping
    BankResponse createBank(
            @RequestParam("name") String name
    );

    @ApiOperation("обновить логотип банка")
    @PostMapping("/{bankId}/logo")
    BankResponse updateLogo(@PathVariable UUID bankId,
                            @RequestParam("logo") MultipartFile logo
    );

    @ApiOperation("получить все банки")
    @GetMapping("/all")
    List<BankResponse> getAllBank(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "name") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortOrder);

    @ApiOperation("получить банк по id")
    @GetMapping("/{id}")
    BankResponse getBankById(@PathVariable UUID bankId);

    @ApiOperation("получить logo по id банка")
    @GetMapping("/{bankId}/logo")
    MultipartFile getLogoBankById(@PathVariable UUID bankId);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{id}")
    void deleteBank(@PathVariable UUID bankId);

    @ApiOperation("обновить name банка по id банка")
    @PutMapping("/{id}")
    BankResponse updateBankName(
            @PathVariable UUID id,
            @NonNull @RequestParam("name") String name);


}
