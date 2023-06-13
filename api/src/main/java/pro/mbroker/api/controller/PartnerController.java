package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.PartnerResponse;

import java.util.List;
import java.util.UUID;

@Api(value = "API Застройщика", tags = "API Застройщика")
@RestController
@RequestMapping("/public/partner")
public interface PartnerController {
    @ApiOperation("создать застройщика")
    @PostMapping
    PartnerResponse createPartner(
            @RequestBody PartnerRequest request
    );

    @ApiOperation("получить всех партнеров")
    @GetMapping("/all")
    List<PartnerResponse> getAllPartner(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "name") String sortBy,
                                        @RequestParam(defaultValue = "asc") String sortOrder);

    @ApiOperation("получить партнера по id")
    @GetMapping("/{partnerId}")
    PartnerResponse getPartnerResponseById(@PathVariable UUID partnerId);

    @ApiOperation("обновить партнера по id")
    @PutMapping("/{partnerId}")
    PartnerResponse updatePartnerById(
            @PathVariable UUID partnerId,
            @RequestBody PartnerRequest request);

    @ApiOperation("получить действующего застройщика")
    @GetMapping("/current")
    PartnerResponse getCurrentPartner();

    @ApiOperation("получить застройщика по id заявки застройщика")
    @GetMapping("/current/{partnerApplicationId}")
    PartnerResponse getPartnerApplicationPartner(@PathVariable UUID partnerApplicationId);

    @ApiOperation("удалить партнера по id")
    @DeleteMapping("/{partnerId}")
    void deletePartner(
            @PathVariable(value = "partnerId") UUID partnerId
    );
}
