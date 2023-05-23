package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankApplicationController;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BorrowerProfileService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankApplicationControllerImpl implements BankApplicationController {
    private final BankApplicationService bankApplicationService;
    private final BankApplicationMapper bankApplicationMapper;
    private final BorrowerProfileService borrowerProfileService;

    @Override
    public BankApplicationResponse getBankApplicationById(UUID bankApplicationId) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        BankApplicationResponse bankApplicationResponse = bankApplicationMapper.toBankApplicationResponse(bankApplication);
        bankApplicationResponse.setCoBorrowers(borrowerProfileService.getBorrowersByPartnerApplicationId(bankApplicationId).getCoBorrower());
        return bankApplicationResponse;
    }
}
