package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.response.SignatureFormStatusResponse;

import java.util.UUID;

@Api(value = "API статусов", tags = "API статусов")
@RestController
@RequestMapping("/public/status")
public interface StatusController {

    @ApiOperation("Подписаны ли все анкеты у заемщиков и со заемщиков по одному из borrowerId")
    @GetMapping("/{borrowerId}/application")
    SignatureFormStatusResponse isApplicationFullySigned(@PathVariable UUID borrowerId);
}