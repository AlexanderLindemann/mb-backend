package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankResponse;

import java.util.UUID;

public interface BankService {
    BankResponse createBank(String name, MultipartFile logoFile);

    BankResponse getBankById(UUID id);

    BankResponse addBankContact(UUID id, String fullName, String email);

    void deleteBankById(UUID id);

    BankResponse deleteBankContact(UUID contactId);

    BankResponse updateBank(UUID id, String request);

    Long upload(MultipartFile file);

    MultipartFile getLogoBankById(UUID bankId);

    MultipartFile download(Long attachmentId);
}
