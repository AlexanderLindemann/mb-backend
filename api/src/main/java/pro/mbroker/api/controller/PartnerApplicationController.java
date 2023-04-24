package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.PartnerApplicationDto;

import java.util.List;

@Api(value = "API Заявок Застройщика", tags = "API Заявок Застройщика")
@RestController
@RequestMapping("/public/partner_application")
public interface PartnerApplicationController {


    @ApiOperation("получить все заявки действующего партнера")
    @GetMapping()
    List<PartnerApplicationDto> getAllPartnerApplication(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "fullName") String sortBy,
                                                         @RequestParam(defaultValue = "asc") String sortOrder);
}
