package pro.mbroker.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    BankResponse getBankById(@PathVariable UUID bankId);

    @ApiOperation("получить logo по id банка")
    @GetMapping("/{bankId}/logo")
    MultipartFile getLogoBankById(@PathVariable UUID bankId);

    @ApiOperation("добавить контакт для банка")
    @PostMapping("/{id}/contacts")
    BankResponse addBankContact(
            @PathVariable UUID id,
            @RequestParam("full_name") String fullName,
            @RequestParam("email") String email);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{id}")
    void deleteBank(@PathVariable UUID id);

    @ApiOperation("удалить контакт банка по id контакта")
    @DeleteMapping("/{bankId}/contacts/{contactId}")
    BankResponse deleteBankContact(
            @PathVariable UUID contactId);

    @ApiOperation("обновить name банка по id банка")
    @PutMapping("/{id}")
    BankResponse updateBank(
            @PathVariable UUID id,
            @NonNull @RequestParam("name") String name);

    @PostMapping("/attachment")
    void upload(@RequestPart("file") MultipartFile file);

    @ApiOperation("получить logo по attachment_id")
    @PostMapping("/{attachmentId}")
    MultipartFile download(@PathVariable Long attachmentId);


}
