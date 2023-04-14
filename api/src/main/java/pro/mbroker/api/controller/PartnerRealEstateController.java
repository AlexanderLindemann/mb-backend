package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.RealEstateAddressRequest;
import pro.mbroker.api.dto.response.PartnerResponse;
import pro.mbroker.api.dto.response.RealEstateAddressResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Api(value = "API Адресов ЖК Застройщика", tags = "API Адресов ЖК Застройщика")
@RestController
@RequestMapping("/public/real_estate_partner")
public interface PartnerRealEstateController {

    @ApiOperation("добавить адрес застройщика")
    @PostMapping("/{partnerId}/")
    PartnerResponse addRealEstateAddress(
            @PathVariable UUID partnerId,
            @RequestBody RealEstateAddressRequest request
    );

    @ApiOperation("удалить адрес застройщика по id")
    @DeleteMapping("/{realEstateId}/")
    void deleteRealEstateAddress(
            @PathVariable UUID realEstateId
    );

    @ApiOperation("обновить данные по ЖК застройщика")
    @PutMapping("/{realEstateId}")
    PartnerResponse updateRealEstateAddress(@PathVariable UUID realEstateId, @RequestBody @Valid RealEstateAddressRequest request);

    @ApiOperation("получить все ЖК по id застройщика")
    @GetMapping("/{partnerId}/real_estate")
    List<RealEstateAddressResponse> getRealEstateAddressByPartnerId(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "name") String sortBy,
                                                                    @RequestParam(defaultValue = "asc") String sortOrder,
                                                                    @PathVariable UUID partnerId);
}
