package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.BankController;
import pro.mbroker.api.dto.BankContactRequest;
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
    public ResponseEntity<BankResponse> createBank(String name, MultipartFile logoFile) {
        return new ResponseEntity<>(bankService.createBank(name, logoFile), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BankResponse> getBankById(UUID id) {
        return new ResponseEntity<>(bankService.getBankById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BankResponse> addBankContact(UUID id, BankContactRequest contactRequest) {
        return new ResponseEntity<>(bankService.addBankContact(id, contactRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteBank(UUID id) {
        bankService.deleteBankById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<BankResponse> deleteBankContact(UUID contactId) {
        return new ResponseEntity<>(bankService.deleteBankContact(contactId), HttpStatus.NO_CONTENT);
    }
}
