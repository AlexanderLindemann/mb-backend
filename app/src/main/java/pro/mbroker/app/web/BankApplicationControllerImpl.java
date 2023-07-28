package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankApplicationController;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.NotificationStatusRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.CalculatorService;

import javax.validation.constraints.NotNull;
import java.util.List;
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
    public BankApplicationResponse changeStatus(UUID bankApplicationId, BankApplicationStatus status) {
        BankApplication bankApplication = bankApplicationService.changeStatus(bankApplicationId, status);
        return toResponse(bankApplication);
    }

    @Override
    public ResponseEntity<String> updateStatuses(NotificationStatusRequest notificationStatusRequest) {
        List<BankApplication> bankApplicationByApplicationId = bankApplicationService
                .getBankApplicationByApplicationId(notificationStatusRequest.getApplications().keySet());

        bankApplicationByApplicationId.forEach(bankApplication -> {
            BankApplicationStatus newStatus = notificationStatusRequest.getApplications()
                    .get(bankApplication.getApplicationNumber());
            bankApplication.setBankApplicationStatus(newStatus);
        });
        bankApplicationService.saveAll(bankApplicationByApplicationId);
        return ResponseEntity.ok().build();
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
