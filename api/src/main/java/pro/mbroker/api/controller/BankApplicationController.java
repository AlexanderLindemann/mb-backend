package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.response.BankApplicationResponse;

import java.util.UUID;

@Api(value = "API Банковской Заявки", tags = "API Банковской Заявки")
@RestController
@RequestMapping("/public/v1/bank_application")
public interface BankApplicationController {

    @ApiOperation("получить банковскую заявку по id")
    @GetMapping("/{bankApplicationId}")
    BankApplicationResponse getBankApplicationById(@PathVariable UUID bankApplicationId);

    @ApiOperation("поменять главного заемщика")
    @PutMapping("/{bankApplicationId}")
    BankApplicationResponse changeMainBorrowerByBankApplicationId(
            @PathVariable UUID bankApplicationId,
            @NonNull @RequestParam("newMainBorrowerId") UUID newMainBorrowerId);

}
