package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerFilter;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.smartdeal.common.security.Permission;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Api(value = "API Заявок Застройщика", tags = "API Заявок Застройщика")
@RestController
@RequestMapping("/public/partner_application")
public interface PartnerApplicationController {


    @ApiOperation("получить все заявки действующего партнера")
    @GetMapping()
    Page<PartnerApplicationResponse> getAllPartnerApplication(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                              @RequestParam(defaultValue = "asc") String sortOrder,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                              @NotNull @RequestParam("permissionCode") Permission permission);


    @GetMapping("/{partnerApplicationId}")
    @ApiOperation("получить заявку по id")
    PartnerApplicationResponse getPartnerApplicationById(@PathVariable UUID partnerApplicationId,
                                                         @RequestParam Permission permission);

    @ApiOperation("Создать заявку")
    @PostMapping()
    PartnerApplicationResponse createPartnerApplication(
            @ApiParam(value = "Параметры кредитной заявки")
            @RequestBody PartnerApplicationRequest request);


    @ApiOperation("обновить заявку")
    @PutMapping("/{partnerApplicationId}")
    PartnerApplicationResponse updatePartnerApplication(
            @PathVariable UUID partnerApplicationId,
            @RequestBody PartnerApplicationRequest request,
            @RequestParam Permission permission);

    @ApiOperation("удалить заявку по id")
    @DeleteMapping("/{partnerApplicationId}")
    void deletePartnerApplication(
            @PathVariable(value = "partnerApplicationId") UUID partnerApplicationId
    );

    @GetMapping("search")
    List<PartnerApplicationResponse> filter(@RequestParam PartnerFilter filter,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String sortDirection);

    @ApiOperation("включить программу банка")
    @PutMapping("/enable_bank_application/{partnerApplicationId}")
    PartnerApplicationResponse enableBankApplication(
            @PathVariable UUID partnerApplicationId,
            @ApiParam(value = "заявка банка") @RequestBody BankApplicationUpdateRequest request);

    @ApiOperation("исключить программу банка")
    @PutMapping("/disable_bank_application/{partnerApplicationId}")
    PartnerApplicationResponse disableBankApplication(
            @PathVariable UUID partnerApplicationId,
            @NotNull @RequestParam("creditProgramId") UUID creditProgramId);

    @GetMapping("/{partnerApplicationId}/required_document")
    @ApiOperation("получить требуемые документы по id заявки")
    List<RequiredDocumentResponse> getRequiredDocuments(@PathVariable UUID partnerApplicationId);

    @ApiOperation("поменять главного заемщика")
    @PutMapping("/{partnerApplicationId}/change_status")
    PartnerApplicationResponse changeMainBorrowerByPartnerApplication(
            @PathVariable UUID partnerApplicationId,
            @NotNull @RequestParam("newMainBorrowerId") UUID newMainBorrowerId);
}
