package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.response.BankApplicationResponse;

import java.util.UUID;

@Api(value = "API Банковской Заявки", tags = "API Банковской Заявки")
@RestController
@RequestMapping("/public/bank_application")
public interface BankApplicationController {

    @ApiOperation("получить банковскую заявку по id")
    @GetMapping("/{bankApplicationId}")
    BankApplicationResponse getBankApplicationById(@PathVariable UUID bankApplicationId);

}
