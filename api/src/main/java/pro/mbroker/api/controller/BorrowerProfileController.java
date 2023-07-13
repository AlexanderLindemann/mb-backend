package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerResponse;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Api(value = "API Профилей заемщика", tags = "API Профилей заемщика")
@RestController
@RequestMapping("/public/borrower_profile")
public interface BorrowerProfileController {
    @ApiOperation("Дополнить или обновить банковскую заявку")
    @PostMapping("/bank_application")
    BorrowerResponse createOrUpdateBorrowerProfile(@ApiParam(value = "Параметры заемщиков") @RequestBody BorrowerRequest request);

    @ApiOperation("Дополнить или обновить обобщенную банковскую заявку")
    @PostMapping("/partner_application")
    BorrowerResponse createOrUpdateGenericBorrowerProfile(@ApiParam(value = "Параметры заемщиков") @RequestBody BorrowerRequest request);

    @ApiOperation("получить обобщенный профиль клиента по id заявки партнера")
    @GetMapping("/{partnerApplicationId}")
    BorrowerResponse getBorrowersByPartnerApplicationId(@PathVariable UUID partnerApplicationId);

    @ApiOperation("удалить профиль клиента по id")
    @DeleteMapping("/{borrowerProfileId}")
    void deleteBorrowerProfileById(
            @NotNull @PathVariable(value = "borrowerProfileId") UUID borrowerProfileId
    );

    @ApiOperation("Пометить загруженный документ клиента, как не активный по id документа")
    @PutMapping(value = "/{attachmentId}/mark_document_as_inactive")
    void deleteBorrowerDocument(@ApiParam(value = "Идентификатор документа") @PathVariable Long attachmentId);
}
