package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankContactController;
import pro.mbroker.api.dto.response.BankContactResponse;
import pro.mbroker.app.entity.BankContact;
import pro.mbroker.app.mapper.BankContactMapper;
import pro.mbroker.app.service.BankContactService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankContactControllerImpl implements BankContactController {

    private final BankContactService bankContactService;
    private final BankContactMapper bankContactMapper;

    @Override
    public void deleteBankContact(UUID contactId, Integer sdId) {
        bankContactService.deleteBankContact(contactId, sdId);
    }

    @Override
    public List<BankContactResponse> getBankContact(UUID bankId) {
        List<BankContact> bankContact = bankContactService.getBankContact(bankId);
        return bankContactMapper.toBankContactResponseListMapper(bankContact);
    }
}
