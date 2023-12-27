package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.response.AttachmentResponse;
import pro.mbroker.app.entity.Bank;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BankService {
    Bank createBank(BankRequest bankRequest);

    Bank getBankById(UUID id);

    void deleteBankById(UUID id);

    Bank updateBank(UUID bankId, BankRequest bankRequest);

    Bank updateLogo(UUID bankId, MultipartFile logo);

    AttachmentResponse getLogoBankById(UUID bankId);

    List<Bank> getAllBank(int page, int size, String sortBy, String sortOrder);

    List<Bank> getAllBankByIds(Set<UUID> bankIds);

    Bank findBankByCianId(Integer cianId);
}
