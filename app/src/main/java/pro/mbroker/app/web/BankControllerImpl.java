package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BankController;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.response.BankResponse;
import pro.mbroker.api.dto.response.StorageResponse;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.FileStorage;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.mapper.CreditProgramMapper;
import pro.mbroker.app.mapper.StorageMapper;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.util.CreditProgramConverter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankControllerImpl implements BankController {

    private final BankService bankService;
    private final AttachmentService attachmentService;
    private final BankMapper bankMapper;
    private final StorageMapper storageMapper;
    private final CreditProgramMapper creditProgramMapper;

    @Override
    public BankResponse createBank(BankRequest bankRequest, Integer sdId) {
        Bank bank = bankService.createBank(bankRequest, sdId);
        return convertToBankResponse(bank);
    }

    @Override
    public BankResponse updateLogo(UUID bankId, UUID fileStorageId, Integer sdId) {
        Bank bank = bankService.updateLogo(bankId, fileStorageId, sdId);
        return convertToBankResponse(bank);
    }

    @Override
    public List<BankResponse> getAllBank(int page, int size, String sortBy, String sortOrder) {
        List<Bank> banks = bankService.getAllBank(page, size, sortBy, sortOrder);
        return banks.stream().map(this::convertToBankResponse).collect(Collectors.toList());
    }

    @Override
    public BankResponse getBankById(UUID bankId) {
        Bank bank = bankService.getBankById(bankId);
        return convertToBankResponse(bank);
    }

    @Override
    public StorageResponse getLogoBankById(UUID bankId) {
        FileStorage fileStorage = bankService.getLogoBankById(bankId);
        return storageMapper.toStorageResponse(fileStorage);
    }

    @Override
    public BankResponse updateBank(UUID bankId, BankRequest bankRequest, Integer sdId) {
        Bank bank = bankService.updateBank(bankId, bankRequest, sdId);
        return convertToBankResponse(bank);
    }

    @Override
    public void deleteBankById(UUID bankId, Integer sdId) {
        bankService.deleteBankById(bankId, sdId);
    }

    private BankResponse convertToBankResponse(Bank bank) {
        BankResponse bankResponse = bankMapper.toBankResponseMapper(bank);
        FileStorage fileStorage = bank.getLogoFileStorage();
        if (Objects.nonNull(bank.getLogoFileStorage())) {
            bankResponse.setUrl(attachmentService.getSignedUrl(fileStorage.getObjectKey()));
            bankResponse.setAttachment(storageMapper.toAttachmentResponse(bank.getLogoFileStorage()));
        }
        if (Objects.nonNull(bank.getCreditPrograms())) {
            bankResponse.setCreditProgram(bank.getCreditPrograms().stream()
                    .map(creditProgram -> creditProgramMapper.toProgramResponseMapper(creditProgram)
                            .setCreditProgramDetail(CreditProgramConverter.convertCreditDetailToEnumFormat(creditProgram.getCreditProgramDetail())))
                    .collect(Collectors.toList()));
        }
        return bankResponse;
    }
}
