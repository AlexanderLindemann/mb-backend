package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankContactController;
import pro.mbroker.api.dto.request.BankContactRequest;
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
    private final BankContactMapper bankContactMapper;

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deleteBankContact(UUID contactId) {
        bankContactService.deleteBankContact(contactId);
    }

    @Override
    public List<BankContactResponse> getBankContact(UUID bankId) {
        List<BankContact> bankContact = bankContactService.getBankContact(bankId);
        return bankContactMapper.toBankContactResponseListMapper(bankContact);
    }

}
