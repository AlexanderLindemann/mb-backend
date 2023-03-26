package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.BankController;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.api.dto.BankRequest;
import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.service.BankService;

import java.util.UUID;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BankControllerImpl implements BankController {

    private final BankService bankService;

    @Override
    public BankResponse createBank(String name, MultipartFile logo) {
        return bankService.createBank(name, logo);
    }

    @Override
    public BankResponse getBankById(UUID id) {
        return bankService.getBankById(id);
    }

    @Override
    public BankResponse addBankContact(UUID id, BankContactRequest contactRequest) {
        return bankService.addBankContact(id, contactRequest);
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
    public BankResponse updateBank(UUID id, BankRequest request) {
        return bankService.updateBank(id, request);
    }

    @Override
    public void upload(MultipartFile file) {
        bankService.upload(file);
    }
}
