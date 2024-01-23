package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    PartnerResponse createPartner(@RequestBody PartnerRequest request, Integer sdId);

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
    PartnerResponse updatePartnerById(@PathVariable UUID partnerId,
                                      @RequestBody PartnerRequest request, Integer sdId);

    @ApiOperation("получить действующего застройщика")
    @GetMapping("/current")
    PartnerResponse getCurrentPartner(Integer organisationId);

    @ApiOperation("получить застройщика по id заявки застройщика")
    @GetMapping("/current/{partnerApplicationId}")
    PartnerResponse getPartnerApplicationPartner(@PathVariable UUID partnerApplicationId);

    @ApiOperation("удалить партнера по id")
    @DeleteMapping("/{partnerId}")
    void deletePartner(@PathVariable(value = "partnerId") UUID partnerId, Integer sdId);
}
