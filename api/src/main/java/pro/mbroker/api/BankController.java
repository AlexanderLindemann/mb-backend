package pro.mbroker.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankResponse;

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
    List<BankResponse> getAllBank();

    @ApiOperation("получить банк по id")
    @GetMapping("/{id}")
    BankResponse getBankById(@PathVariable UUID bankId);

    @ApiOperation("получить logo по id банка")
    @GetMapping("/{bankId}/logo")
    MultipartFile getLogoBankById(@PathVariable UUID bankId);

    @ApiOperation("получить logo по attachment_id")
    @PostMapping("/{attachmentId}")
    MultipartFile download(@PathVariable Long attachmentId);

    @ApiOperation("добавить контакт для банка")
    @PostMapping("/{id}/contacts")
    BankResponse addBankContact(
            @PathVariable UUID bankId,
            @RequestParam("full_name") String fullName,
            @RequestParam("email") String email);

    @ApiOperation("удалить банк по id")
    @DeleteMapping("/{id}")
    void deleteBank(@PathVariable UUID bankId);

    @ApiOperation("удалить контакт банка по id контакта")
    @DeleteMapping("/{bankId}/contacts/{contactId}")
    BankResponse deleteBankContact(
            @PathVariable UUID contactId);

    @ApiOperation("обновить name банка по id банка")
    @PutMapping("/{id}")
    BankResponse updateBankName(
            @PathVariable UUID id,
            @NonNull @RequestParam("name") String name);

    @PostMapping("/attachment")
    void upload(@RequestPart("file") MultipartFile file);


}
