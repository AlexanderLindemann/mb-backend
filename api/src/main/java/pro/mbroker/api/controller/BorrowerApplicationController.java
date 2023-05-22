package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;

import java.util.UUID;

@Api(value = "API Профилей клиента", tags = "API Профилей клиента")
@RestController
@RequestMapping("/public/borrower_profil")
public interface BorrowerApplicationController {
    @ApiOperation("Дополнить или обновить банковскую заявку")
    @PostMapping()
    BorrowerResponse createOrUpdateBorrowerApplication(@ApiParam(value = "Параметры заемщиков") @RequestBody BorrowerRequest request);

    @ApiOperation("получить профили клиентов по id банковской заявки")
    @GetMapping("/{bankApplicationId}")
    BorrowerResponse getBorrowersByBankApplicationId(@PathVariable UUID bankApplicationId);
}
