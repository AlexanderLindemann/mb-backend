package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.response.BankResponse;

import java.util.UUID;

@Api(value = "API Контактов Банка", tags = "API Контактов Банка")
@RestController
@RequestMapping("/public/bank_contact")
public interface BankContactController {

    @ApiOperation("добавить контакт для банка")
    @PostMapping("/{id}/contacts")
    BankResponse addBankContact(
            @PathVariable UUID bankId,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email);

    @ApiOperation("удалить контакт банка по id контакта")
    @DeleteMapping("/{bankId}/contacts/{contactId}")
    BankResponse deleteBankContact(
            @PathVariable UUID contactId);


}
