package pro.mbroker.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.api.dto.BankRequest;
import pro.mbroker.api.dto.BankResponse;

import java.util.UUID;

@Api("API Банка")
@RestController
@RequestMapping("/public/bank")
public interface BankController {
    @ApiOperation("создать банк")
    @PostMapping
    BankResponse createBank(
            @RequestParam("name") String name,
            @RequestParam("logo") MultipartFile logo
    );

    @ApiOperation("получить банк по id")
    @GetMapping("/{id}")
    BankResponse getBankById(@PathVariable UUID id);

    @ApiOperation("добавить контакт для банка")
    @PostMapping("/{id}/contacts")
    BankResponse addBankContact(
            @PathVariable UUID id,
            @ModelAttribute BankContactRequest contactRequest);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{id}")
    void deleteBank(@PathVariable UUID id);

    @ApiOperation("удалить контакт банка по id контакта")
    @DeleteMapping("/{bankId}/contacts/{contactId}")
    BankResponse deleteBankContact(
            @PathVariable UUID contactId);

    @ApiOperation("обновить банк по id банка - отправив BankRequest с новым полем name или logo")
    @PutMapping("/{id}")
    BankResponse updateBank(
            @PathVariable UUID id,
            @RequestBody BankRequest request);

    @PostMapping("/attachment")
    void upload(@RequestPart("file") MultipartFile file);


}
