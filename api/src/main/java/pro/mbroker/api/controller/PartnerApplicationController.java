package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;

import java.util.List;
import java.util.UUID;

@Api(value = "API Заявок Застройщика", tags = "API Заявок Застройщика")
@RestController
@RequestMapping("/public/partner_application")
public interface PartnerApplicationController {


    @ApiOperation("получить все заявки действующего партнера")
    @GetMapping()
    List<PartnerApplicationResponse> getAllPartnerApplication(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                              @RequestParam(defaultValue = "asc") String sortOrder);

    @GetMapping("/{partnerApplicationId}")
    @ApiOperation("получить заявку по id")
    PartnerApplicationResponse getPartnerApplicationById(@PathVariable UUID partnerApplicationId);

    @ApiOperation("Создать заявку")
    @PostMapping()
    PartnerApplicationResponse createPartnerApplication(@ApiParam(value = "Параметры кредитной заявки") @RequestBody PartnerApplicationRequest request);


    @ApiOperation("обновить заявку")
    @PutMapping("/{partnerApplicationId}")
    PartnerApplicationResponse updatePartnerApplication(
            @PathVariable UUID partnerApplicationId,
            @RequestBody PartnerApplicationRequest request);

    @ApiOperation("удалить заявку по id")
    @DeleteMapping("/{partnerApplicationId}")
    void deletePartnerApplication(
            @PathVariable(value = "partnerApplicationId") UUID partnerApplicationId
    );

    @GetMapping("search")
    List<BankApplicationResponse> filter(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String residentialComplexName,
            @RequestParam(required = false) RegionType region,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) BankApplicationStatus applicationStatus,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    );
}
