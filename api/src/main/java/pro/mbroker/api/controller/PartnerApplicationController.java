package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
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
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Api(value = "API Заявок Застройщика", tags = "API Заявок Застройщика")
@RestController
@RequestMapping("/public/partner_application")
@SuppressWarnings("PMD")
public interface PartnerApplicationController {
    @ApiOperation("получить все заявки действующего партнера")
    @PostMapping("/service-api-get-all-partner-application")
    Page<PartnerApplicationResponse> getAllPartnerApplication(@RequestBody PartnerApplicationServiceRequest request);

    @GetMapping("/{partnerApplicationId}")
    @ApiOperation("получить заявку по id")
    PartnerApplicationResponse getPartnerApplicationById(@PathVariable UUID partnerApplicationId);

    @ApiOperation("Создать заявку")
    @PostMapping()
    PartnerApplicationResponse createPartnerApplication(@ApiParam(value = "Параметры кредитной заявки")
                                                        @RequestBody PartnerApplicationRequest request, Integer sdId);

    @ApiOperation("обновить заявку")
    @PutMapping("/{partnerApplicationId}")
    PartnerApplicationResponse updatePartnerApplication(@PathVariable UUID partnerApplicationId,
                                                        @RequestBody PartnerApplicationRequest request, Integer sdId);

    @ApiOperation("Изменить активность заявки")
    @PutMapping("/{partnerApplicationId}/change-active-status")
    void changePartnerApplicationActiveStatus(@PathVariable UUID partnerApplicationId,
                                              @RequestBody boolean isActive, Integer sdId);

    @ApiOperation("включить программу банка")
    @PutMapping("/enable_bank_application/{partnerApplicationId}")
    PartnerApplicationResponse enableBankApplication(@PathVariable UUID partnerApplicationId,
                                                     @ApiParam(value = "заявка банка") @RequestBody BankApplicationUpdateRequest request,
                                                     Integer sdId);

    @ApiOperation("исключить программу банка")
    @PutMapping("/disable_bank_application/{partnerApplicationId}")
    PartnerApplicationResponse disableBankApplication(@PathVariable UUID partnerApplicationId,
                                                      @NotNull @RequestParam("creditProgramId") UUID creditProgramId, Integer sdId);

    @GetMapping("/{partnerApplicationId}/required_document")
    @ApiOperation("получить требуемые документы по id заявки")
    List<RequiredDocumentResponse> getRequiredDocuments(@PathVariable UUID partnerApplicationId);

    @ApiOperation("поменять главного заемщика")
    @PutMapping("/{partnerApplicationId}/change_status")
    PartnerApplicationResponse changeMainBorrowerByPartnerApplication(@PathVariable UUID partnerApplicationId,
                                                                      @NotNull @RequestParam("newMainBorrowerId") UUID newMainBorrowerId,
                                                                      Integer sdId);

    @GetMapping("/by-attachment/{attachmentId}")
    @ApiOperation("получить партнера по attachmentId")
    PartnerApplicationResponse getPartnerApplicationByAttachmentId(@PathVariable Long attachmentId);

    @GetMapping("/by-phoneNumber/{phoneNumber}")
    @ApiOperation("получить партнера по phoneNumber")
    List<PartnerApplicationResponse> findPartnerApplicationByPhoneNumber(@PathVariable String phoneNumber);
}
