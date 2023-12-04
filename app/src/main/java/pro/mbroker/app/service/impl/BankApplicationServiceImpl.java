package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.repository.BankApplicationRepository;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.StatusService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankApplicationServiceImpl implements BankApplicationService {
    private final StatusService statusService;
    private final BankApplicationRepository bankApplicationRepository;
    private final BankApplicationMapper bankApplicationMapper;
    private final BorrowerProfileRepository borrowerProfileRepository;

    public static final Set<BankApplicationStatus> UNCHANGEABLE_STATUSES = Set.of(
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
    @Transactional
    public BankApplication changeStatus(UUID bankApplicationId, BankApplicationStatus status) {
        BankApplication bankApplication = getBankApplicationById(bankApplicationId)
                .setBankApplicationStatus(status);
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        statusService.statusChanger(partnerApplication);
        return bankApplicationRepository.save(bankApplication);
    }

    @Override
    public List<BankApplication> getBankApplicationByApplicationId(Collection<Integer> applicationNumbers) {
        return bankApplicationRepository.findAllByApplicationNumberIn(applicationNumbers);
    }

    @Override
    public List<BankApplication> getBankApplicationsByPartnerApplicationId(UUID partnerApplicationId) {
        return bankApplicationRepository.findAllByPartnerApplicationId(partnerApplicationId);
    }

    @Transactional
    @Override
    public void saveAll(Collection<BankApplication> bankApplications) {
        bankApplicationRepository.saveAll(bankApplications);
    }

    @Transactional
    @Override
    public void save(BankApplication bankApplications) {
        bankApplicationRepository.save(bankApplications);
    }


    @Override
    public BankApplication updateBankApplication(BankApplicationRequest request) {
        return bankApplicationMapper.updateBankApplicationFromRequest(getBankApplicationById(request.getId()), request);
    }

    @Transactional(readOnly = true)
    public List<BankApplication> getBankApplicationByBorrowerId(UUID borrowerId) {
        return bankApplicationRepository.findByMainBorrowerId(borrowerId);
    }

    @Override
    public int updateStatus(UUID bankApplicationId, BankApplicationStatus status) {
        return bankApplicationRepository.updateStatus(bankApplicationId, status);
    }
}
