package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankApplicationController;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.CalculatorService;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankApplicationControllerImpl implements BankApplicationController {
    private final BankApplicationService bankApplicationService;
    private final BorrowerProfileService borrowerProfileService;
    private final CalculatorService calculatorService;
    private final BankApplicationMapper bankApplicationMapper;

    @Override
    public BankApplicationResponse getBankApplicationById(UUID bankApplicationId) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        return toResponse(bankApplication);
    }

    @Override
    public BankApplicationResponse changeMainBorrowerByBankApplicationId(UUID bankApplicationId, @NotNull UUID newMainBorrowerId) {
        BankApplication bankApplication = bankApplicationService.changeMainBorrowerByBankApplicationId(bankApplicationId, newMainBorrowerId);
        return toResponse(bankApplication);
    }

    @Override
    public BankApplicationResponse updateBankApplication(BankApplicationRequest request) {
        BankApplication bankApplication = bankApplicationService.updateBankApplication(request);
        return toResponse(bankApplication);
    }

    private BankApplicationResponse toResponse(BankApplication bankApplication) {
        return bankApplicationMapper.toBankApplicationResponse(bankApplication)
                .setMortgageSum(calculatorService.getMortgageSum(bankApplication.getRealEstatePrice(), bankApplication.getDownPayment()))
                .setCoBorrowers(borrowerProfileService.getBorrowersByBankApplicationId(bankApplication.getId()).getCoBorrower());
    }
}
