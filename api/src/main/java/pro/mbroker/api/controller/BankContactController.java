package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BankContactRequest;
import pro.mbroker.api.dto.response.BankContactResponse;
import pro.mbroker.api.dto.response.BankResponse;

import java.util.List;
import java.util.UUID;

@Api(value = "API Контактов Банка", tags = "API Контактов Банка")
@RestController
@RequestMapping("/public/bank_contact")
public interface BankContactController {

    @ApiOperation("добавить контакт для банка")
    @PostMapping()
    BankResponse addBankContact(
            @RequestBody BankContactRequest request);

    @ApiOperation("удалить контакт банка по contactId")
    @DeleteMapping("/{contactId}")
    BankResponse deleteBankContact(
            @PathVariable UUID contactId);

    @ApiOperation("получить контакты банка по bankId")
    @GetMapping("/{bankId}")
    List<BankContactResponse> getBankContact(
            @PathVariable UUID bankId);


}
