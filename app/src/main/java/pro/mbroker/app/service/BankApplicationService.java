package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BankApplicationService {

    BankApplication getBankApplicationById(UUID id);

    BankApplication updateBankApplication(BankApplicationRequest request, Integer sdId);

    BankApplication changeMainBorrowerByBankApplicationId(UUID bankApplicationId, UUID newMainBorrowerId, Integer sdId);

    BankApplication changeStatus(UUID bankApplicationId, BankApplicationStatus status, Integer sdId);

    List<BankApplication> getBankApplicationByBorrowerId(UUID borrowerId);

    List<BankApplication> getBankApplicationByApplicationId(Collection<Integer> applicationNumbers);

    List<BankApplication> getBankApplicationsByPartnerApplicationId(UUID partnerApplicationId);

    void saveAll(Collection<BankApplication> bankApplications);

    void save(BankApplication bankApplication);

    int updateStatus(UUID bankApplicationId, BankApplicationStatus statu);

}
