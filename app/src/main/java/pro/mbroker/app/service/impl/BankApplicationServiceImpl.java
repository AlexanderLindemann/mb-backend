package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.BankApplicationService;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankApplicationServiceImpl implements BankApplicationService {
    private final BankApplicationRepository bankApplicationRepository;
    private final BankApplicationMapper bankApplicationMapper;
    private final BorrowerProfileRepository borrowerProfileRepository;

    private static final Set<BankApplicationStatus> UNCHANGEABLE_STATUSES = Set.of(
            BankApplicationStatus.SENT_TO_BANK,
            BankApplicationStatus.SENDING_TO_BANK,
            BankApplicationStatus.APPLICATION_APPROVED,
            BankApplicationStatus.CREDIT_APPROVED,
            BankApplicationStatus.REFINEMENT,
            BankApplicationStatus.REJECTED,
            BankApplicationStatus.SENDING_ERROR,
            BankApplicationStatus.EXPIRED
    );

    @Override
    public BankApplication getBankApplicationById(UUID id) {
        return bankApplicationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(BankApplication.class, id));
    }

    @Override
    public BankApplication changeMainBorrowerByBankApplicationId(UUID bankApplicationId, UUID newMainBorrowerId) {
        BankApplication bankApplication = getBankApplicationById(bankApplicationId);
        BorrowerProfile borrowerProfile = borrowerProfileRepository.findById(newMainBorrowerId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, newMainBorrowerId));
        if (!UNCHANGEABLE_STATUSES.contains(bankApplication.getBankApplicationStatus())) {
            bankApplication.setMainBorrower(borrowerProfile);
        }
        return bankApplicationRepository.save(bankApplication);
    }

    @Override
    public BankApplication changeStatus(UUID bankApplicationId, BankApplicationStatus status) {
        BankApplication bankApplication = getBankApplicationById(bankApplicationId)
                .setBankApplicationStatus(status);
        return bankApplicationRepository.save(bankApplication);
    }

    @Override
    public BankApplication updateBankApplication(BankApplicationRequest request) {
        return bankApplicationMapper.updateBankApplicationFromRequest(getBankApplicationById(request.getId()), request);
    }

    @Transactional(readOnly = true)
    public BankApplication getBankApplicationByBorrowerId(UUID borrowerId) {
        return bankApplicationRepository.findByMainBorrowerId(borrowerId)
                .orElseThrow(() -> new ItemNotFoundException(BankApplication.class,
                        "bankApplication with borrowerId не найдено"));
    }
}
