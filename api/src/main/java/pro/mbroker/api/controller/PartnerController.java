package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.request.PartnerRequest;
import pro.mbroker.api.dto.response.PartnerResponse;

@Api("API Застройщика")
@RestController
@RequestMapping("/public/partner")
public interface PartnerController {
    @ApiOperation("создать застройщика")
    @PostMapping
    PartnerResponse createPartner(
            @RequestBody PartnerRequest request
    );


}
