package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileForUpdateResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Api(value = "API Профилей заемщика", tags = "API Профилей заемщика")
@RestController
@RequestMapping("/public/borrower_profile")
public interface BorrowerProfileController {
    @ApiOperation("Дополнить или обновить банковскую заявку")
    @PostMapping("/bank_application")
    BorrowerResponse createOrUpdateBorrowerProfile(@ApiParam(value = "Параметры заемщиков") @RequestBody BorrowerRequest request,
                                                   Integer sdId);

    @ApiOperation("Дополнить или обновить обобщенную банковскую заявку")
    @PostMapping("/partner_application")
    BorrowerResponse createOrUpdateGenericBorrowerProfile(@ApiParam(value = "Параметры заемщиков")
                                                          @RequestBody BorrowerRequest request, Integer sdId);

    @ApiOperation("получить обобщенный профиль клиента по id заявки партнера")
    @GetMapping("/{partnerApplicationId}")
    BorrowerResponse getBorrowersByPartnerApplicationId(@PathVariable UUID partnerApplicationId);

    @ApiOperation("удалить профиль клиента по id")
    @DeleteMapping("/{borrowerProfileId}")
    void deleteBorrowerProfileById(@NotNull @PathVariable(value = "borrowerProfileId") UUID borrowerProfileId,
                                   Integer sdId);

    @ApiOperation("Пометить загруженный документ клиента, как не активный по id документа")
    @PutMapping(value = "/{attachmentId}/mark_document_as_inactive")
    void deleteBorrowerDocument(@ApiParam(value = "Идентификатор документа") @PathVariable Long attachmentId,
                                Integer sdId);

    @ApiOperation("Добавить поле в профиль клиента")
    @PutMapping("/{borrowerProfileId}/updateField")
    void updateBorrowerProfileField(@ApiParam(value = "Идентификатор профиля") @PathVariable UUID borrowerProfileId,
                                    @RequestBody Map<String, Object> fieldsMap);

    @ApiOperation("получить полный профиль клиента")
    @GetMapping("/{borrowerProfileId}/full")
    BorrowerProfileForUpdateResponse getFullBorrower(@PathVariable UUID borrowerProfileId);

}
