package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;

@Api(value = "API калькулятора", tags = "API калькулятора")
@RestController
@RequestMapping("/public/calculator")
public interface CalculatorController {

    @ApiOperation("Получить предложения от банков")
    @PostMapping()
    PropertyMortgageDTO getCreditOffer(@RequestBody CalculatorRequest request);
}
