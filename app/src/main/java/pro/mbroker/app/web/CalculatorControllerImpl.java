package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.CalculatorController;
import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.app.service.CalculatorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculatorControllerImpl implements CalculatorController {
    private final CalculatorService calculatorService;

    @Override
    public PropertyMortgageDTO getCreditOffer(CalculatorRequest request) {
        return calculatorService.getCreditOffer(request);
    }
}
