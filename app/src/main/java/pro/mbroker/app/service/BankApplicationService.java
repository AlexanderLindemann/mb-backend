package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;

import java.util.UUID;

public interface BankApplicationService {

    BankApplication getBankApplicationById(UUID id);

    BankApplication updateBankApplication(BankApplicationRequest request);

    BankApplication changeMainBorrowerByBankApplicationId(UUID bankApplicationId, UUID newMainBorrowerId);

    BankApplication changeStatus(UUID bankApplicationId, BankApplicationStatus status);
}
