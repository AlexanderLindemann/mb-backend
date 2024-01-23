package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.response.BankContactResponse;

import java.util.List;
import java.util.UUID;

@Api(value = "API Контактов Банка", tags = "API Контактов Банка")
@RestController
@RequestMapping("/public/bank_contact")
public interface BankContactController {

    @ApiOperation("удалить контакт банка по contactId")
    @DeleteMapping("/{contactId}")
    void deleteBankContact(@PathVariable UUID contactId, Integer sdId);

    @ApiOperation("получить контакты банка по bankId")
    @GetMapping("/{bankId}")
    List<BankContactResponse> getBankContact(@PathVariable UUID bankId);
}
