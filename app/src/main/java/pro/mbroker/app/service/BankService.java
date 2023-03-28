package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankResponse;

import java.util.List;
import java.util.UUID;

public interface BankService {
    BankResponse createBank(String name);

    BankResponse getBankById(UUID id);

    BankResponse addBankContact(UUID id, String fullName, String email);

    void deleteBankById(UUID id);

    BankResponse deleteBankContact(UUID contactId);

    BankResponse updateBankName(UUID id, String request);

    BankResponse updateLogo(UUID bankId, MultipartFile logo);

    MultipartFile getLogoBankById(UUID bankId);

    MultipartFile download(Long attachmentId);

    Long upload(MultipartFile file);

    List<BankResponse> getAllBank();
}
