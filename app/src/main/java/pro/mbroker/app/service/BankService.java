package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.model.bank.Bank;

import java.util.List;
import java.util.UUID;

public interface BankService {
    Bank createBank(String name);

    Bank getBankById(UUID id);

    void deleteBankById(UUID id);

    Bank updateBankName(UUID id, String request);

    Bank updateLogo(UUID bankId, MultipartFile logo);

    MultipartFile getLogoBankById(UUID bankId);

    MultipartFile download(Long attachmentId);

    Long upload(MultipartFile file);

    List<Bank> getAllBank(int page, int size);
}
