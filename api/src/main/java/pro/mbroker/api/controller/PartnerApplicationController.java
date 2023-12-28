package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Api(value = "API Заявок Застройщика", tags = "API Заявок Застройщика")
@RestController
@RequestMapping("/public/partner_application")
@SuppressWarnings("PMD")
public interface PartnerApplicationController {
    @ApiOperation("получить все заявки действующего партнера")
    @GetMapping()
    Page<PartnerApplicationResponse> getAllPartnerApplication(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                              @RequestParam(defaultValue = "asc") String sortOrder,
                                                              @RequestParam(required = false) String fullName,
                                                              @RequestParam(required = false) String phoneNumber,
                                                              @RequestParam(required = false) Integer applicationNumber,
                                                              @RequestParam(required = false) UUID realEstateId,
                                                              @RequestParam(required = false) RegionType region,
                                                              @RequestParam(required = false) UUID bankId,
                                                              @RequestParam(required = false) BankApplicationStatus applicationStatus,
                                                              @RequestParam(defaultValue = "true") Boolean isActive,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate);


    @GetMapping("/{partnerApplicationId}")
    @ApiOperation("получить заявку по id")
    PartnerApplicationResponse getPartnerApplicationById(@PathVariable UUID partnerApplicationId);

    @ApiOperation("Создать заявку")
    @PostMapping()
    PartnerApplicationResponse createPartnerApplication(@ApiParam(value = "Параметры кредитной заявки")
                                                        @RequestBody PartnerApplicationRequest request,
                                                        HttpServletRequest httpRequest);

    @ApiOperation("обновить заявку")
    @PutMapping("/{partnerApplicationId}")
    PartnerApplicationResponse updatePartnerApplication(
            @PathVariable UUID partnerApplicationId,
            @RequestBody PartnerApplicationRequest request);

    @ApiOperation("Изменить активность заявки")
    @PutMapping("/{partnerApplicationId}/change-active-status")
    void changePartnerApplicationActiveStatus(
            @PathVariable UUID partnerApplicationId,
            @RequestBody boolean isActive);

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
