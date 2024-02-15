package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.FileStorage;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BankService {
    Bank createBank(BankRequest bankRequest, Integer sdId);

    Bank getBankById(UUID id);

    void deleteBankById(UUID bankId, Integer sdId);

    Bank updateBank(UUID bankId, BankRequest bankRequest, Integer sdId);

    Bank updateLogo(UUID bankId, UUID fileStorageId, Integer sdId);

    FileStorage getLogoBankById(UUID bankId);

    List<Bank> getAllBank(int page, int size, String sortBy, String sortOrder);

    List<Bank> getAllBankByIds(Set<UUID> bankIds);

    Bank findBankByCianId(Integer cianId);
}
