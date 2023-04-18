package pro.mbroker.app.service;

import pro.mbroker.api.dto.PropertyMortgageDTO;
import pro.mbroker.api.dto.request.CalculatorRequest;

public interface CalculatorService {
    PropertyMortgageDTO getCreditOffer(CalculatorRequest request);
}
