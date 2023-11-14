package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankApplicationController;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.notification.NotificationStatusRequest;
import pro.mbroker.api.dto.request.notification.UnderwritingResponse;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.mapper.underwriting.UnderwritingMapper;
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
    private final UnderwritingMapper underwritingMapper;

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
        log.info("Начали обновление заявок. Количество: " + notificationStatusRequest.getApplications().size());
        StringBuilder result = new StringBuilder();
        StringBuilder fail = new StringBuilder();

        List<BankApplication> bankApplicationByApplications = bankApplicationService
                .getBankApplicationByApplicationId(notificationStatusRequest.getApplications().keySet());

        bankApplicationByApplications.forEach(bankApplication -> {
            UnderwritingResponse underwritingResponse = notificationStatusRequest.getApplications()
                    .get(bankApplication.getApplicationNumber());
            try {
                BankApplicationStatus newStatus = mappingRosBankStatus(underwritingResponse.getDecision().getStatus());
                bankApplicationService.updateStatus(bankApplication.getId(), newStatus);
                bankApplication.setBankApplicationStatus(newStatus);
                bankApplication.setUnderwriting(underwritingMapper.toUnderwriting(underwritingResponse));
                bankApplicationService.save(bankApplication);

            } catch (Exception e) {
                fail.append(bankApplication.getId()).append(", ");
            }

        });

        if (result.length() == 0) {
            result.append("Обновлены статусы заявок в количестве ").append(bankApplicationByApplications.size());
            log.info(result.toString());
        }
        else {
            result.append("Не смогли обновить статус для bankApplications: ");
            result.append(fail);
            log.error(result.toString());
        }

        return ResponseEntity.ok().body(result.toString());
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

    public static BankApplicationStatus mappingRosBankStatus(int creditStatusValue) {
        switch (creditStatusValue) {
            case 1 : {
                return BankApplicationStatus.SENT_TO_BANK;
            }
            case 2 : {
                return BankApplicationStatus.REFINEMENT;
            }
            case 3 :
            case 4 :
            case 5 : {
                return BankApplicationStatus.APPLICATION_APPROVED;
            }
            case 6 :
            case 7 :
            case 10 : {
                return BankApplicationStatus.CREDIT_APPROVED;
            }
            case 8 :
            case 11 :
            case 9 : {
                return BankApplicationStatus.REJECTED;
            }
            default : {
                return null;
            }
        }
    }
}
