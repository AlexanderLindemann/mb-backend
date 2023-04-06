package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.Bank;

import java.util.List;
import java.util.UUID;

public interface BankService {
    Bank createBank(String name);

    Bank getBankById(UUID id);

    void deleteBankById(UUID id);

    Bank updateBankName(UUID id, String request);

    Bank updateLogo(UUID bankId, MultipartFile logo);

    MultipartFile getLogoBankById(UUID bankId);

    List<Bank> getAllBank(int page, int size, String sortBy, String sortOrder);
}
