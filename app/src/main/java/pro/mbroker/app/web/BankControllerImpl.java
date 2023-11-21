package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.BankController;
import pro.mbroker.api.controller.CreditProgramController;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.response.AttachmentResponse;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.util.Converter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankControllerImpl implements BankController {

    private final BankService bankService;
    private final AttachmentService attachmentService;
    private final BankMapper bankMapper;
    private final CreditProgramController creditProgramController;

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public BankResponse createBank(BankRequest bankRequest) {
        Bank bank = bankService.createBank(bankRequest);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public BankResponse updateLogo(UUID bankId, MultipartFile logo) {
        Bank bank = bankService.updateLogo(bankId, logo);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public List<BankResponse> getAllBank(int page, int size, String sortBy, String sortOrder) {
        List<Bank> bankList = bankService.getAllBank(page, size, sortBy, sortOrder);

        return bankList.stream().map(bank -> {
            BankResponse bankResponse = bankMapper.toBankResponseMapper(bank);
            Attachment attachment = bank.getAttachment();
            if (attachment != null) {
                String base64Logo = Converter.generateBase64FromFile(attachmentService.download(attachment.getId()));
                bankResponse.setLogo(base64Logo);
            }
            return bankResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public BankResponse getBankById(UUID bankId) {
        Bank bank = bankService.getBankById(bankId);
        List<BankContact> activeContacts = bank.getContacts().stream()
                .filter(BankContact::isActive)
                .collect(Collectors.toList());
        bank.setContacts(activeContacts);
        return bankMapper.toBankResponseMapper(bank)
                .setCreditProgram(creditProgramController.getProgramsByBankId(bankId));
    }

    @Override
    public AttachmentResponse getLogoBankById(UUID bankId) {
        return bankService.getLogoBankById(bankId);
    }


    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public BankResponse updateBank(UUID bankId, BankRequest bankRequest) {
        Bank bank = bankService.updateBank(bankId, bankRequest);
        List<BankContact> activeContacts = bank.getContacts().stream()
                .filter(BankContact::isActive)
                .collect(Collectors.toList());
        bank.setContacts(activeContacts);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deleteBankById(UUID bankId) {
        bankService.deleteBankById(bankId);
    }

}
