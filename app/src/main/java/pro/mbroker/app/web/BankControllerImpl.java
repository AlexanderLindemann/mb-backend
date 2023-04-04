package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.BankController;
import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.model.bank.Bank;
import pro.mbroker.app.service.BankService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankControllerImpl implements BankController {

    private final BankService bankService;
    private final BankMapper bankMapper;

    @Override
    @Transactional
    public BankResponse createBank(String name) {
        Bank bank = bankService.createBank(name);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    @Transactional
    public BankResponse updateLogo(UUID bankId, MultipartFile logo) {
        Bank bank = bankService.updateLogo(bankId, logo);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankResponse> getAllBank(int page, int size,
                                         String sortBy, String sortOrder) {
        List<Bank> bankList = bankService.getAllBank(page, size, sortBy, sortOrder);
        return bankList.stream()
                .map(bankMapper::toBankResponseMapper)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public BankResponse getBankById(UUID id) {
        Bank bank = bankService.getBankById(id);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public MultipartFile getLogoBankById(UUID bankId) {
        return bankService.getLogoBankById(bankId);
    }


    @Override
    @Transactional
    public void deleteBank(UUID id) {
        bankService.deleteBankById(id);
    }


    @Override
    @Transactional
    public BankResponse updateBankName(UUID id, String name) {
        Bank bank = bankService.updateBankName(id, name);
        return bankMapper.toBankResponseMapper(bank);
    }

}
