package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.RealEstateRequest;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.dto.response.RealEstateResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api(value = "API Адресов ЖК Застройщика", tags = "API Адресов ЖК Застройщика")
@RestController
@RequestMapping("/public/real_estate_partner")
public interface PartnerRealEstateController {

    @ApiOperation("добавить адрес застройщика")
    @PostMapping("/{partnerId}")
    PartnerResponse addRealEstate(
            @PathVariable UUID partnerId,
            @RequestBody RealEstateRequest request
    );

    @ApiOperation("удалить адрес застройщика по id")
    @DeleteMapping("/{realEstateId}")
    void deleteRealEstate(
            @PathVariable(value = "realEstateId") UUID realEstateId
    );

    @ApiOperation("обновить данные по ЖК застройщика")
    @PutMapping("/{realEstateId}")
    PartnerResponse updateRealEstate(@PathVariable UUID realEstateId, @RequestBody @Valid RealEstateRequest request);

    @ApiOperation("получить все ЖК по id застройщика")
    @GetMapping("/{partnerId}/real_estate")
    List<RealEstateResponse> getRealEstateByPartnerId(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "residentialComplexName") String sortBy,
                                                      @RequestParam(defaultValue = "asc") String sortOrder,
                                                      @PathVariable UUID partnerId);

    @ApiOperation("получить все ЖК действующего застройщика")
    @GetMapping("/current_real_estate")
    List<RealEstateResponse> getCurrentRealEstate(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "residentialComplexName") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String sortOrder);
}
