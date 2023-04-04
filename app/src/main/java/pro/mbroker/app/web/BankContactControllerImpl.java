package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.controller.BankContactController;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.model.bank.Bank;
import pro.mbroker.app.service.BankContactService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankContactControllerImpl implements BankContactController {

    private final BankContactService bankContactService;
    private final BankMapper bankMapper;


    @Override
    @Transactional
    public BankResponse addBankContact(UUID id, String fullName, String email) {
        Bank bank = bankContactService.addBankContact(id, fullName, email);
        return bankMapper.toBankResponseMapper(bank);
    }


    @Override
    @Transactional
    public BankResponse deleteBankContact(UUID contactId) {
        Bank bank = bankContactService.deleteBankContact(contactId);
        return bankMapper.toBankResponseMapper(bank);
    }

}
