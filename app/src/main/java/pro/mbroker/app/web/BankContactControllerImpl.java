package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.controller.BankContactController;
import pro.mbroker.api.dto.response.BankContactResponse;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;
import pro.mbroker.app.mapper.BankContactMapper;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.service.BankContactService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankContactControllerImpl implements BankContactController {

    private final BankContactService bankContactService;
    private final BankMapper bankMapper;
    private final BankContactMapper bankContactMapper;


    @Override
    @Transactional
    public BankResponse addBankContact(UUID bankId, String fullName, String email) {
        Bank bank = bankContactService.addBankContact(bankId, fullName, email);
        return bankMapper.toBankResponseMapper(bank);
    }


    @Override
    @Transactional
    public BankResponse deleteBankContact(UUID contactId) {
        Bank bank = bankContactService.deleteBankContact(contactId);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public List<BankContactResponse> getBankContact(UUID bankId) {
        List<BankContact> bankContact = bankContactService.getBankContact(bankId);
        return bankContactMapper.toBankContactResponseListMapper(bankContact);
    }

}
