package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.BankController;
import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.service.BankService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BankControllerImpl implements BankController {

    private final BankService bankService;

    @Override
    public BankResponse createBank(String name) {
        return bankService.createBank(name);
    }

    @Override
    public BankResponse updateLogo(UUID bankId, MultipartFile logo) {
        return bankService.updateLogo(bankId, logo);
    }

    @Override
    public List<BankResponse> getAllBank() {
        return bankService.getAllBank();
    }


    @Override
    public BankResponse getBankById(UUID id) {
        return bankService.getBankById(id);
    }

    @Override
    public MultipartFile getLogoBankById(UUID bankId) {
        return bankService.getLogoBankById(bankId);
    }

    @Override
    public BankResponse addBankContact(UUID id, String fullName, String email) {
        return bankService.addBankContact(id, fullName, email);
    }

    @Override
    public void deleteBank(UUID id) {
        bankService.deleteBankById(id);
    }

    @Override
    public BankResponse deleteBankContact(UUID contactId) {
        return bankService.deleteBankContact(contactId);
    }

    @Override
    public BankResponse updateBankName(UUID id, String name) {
        return bankService.updateBankName(id, name);
    }

    @Override
    public void upload(MultipartFile file) {
        bankService.upload(file);
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        return bankService.download(attachmentId);
    }
}
