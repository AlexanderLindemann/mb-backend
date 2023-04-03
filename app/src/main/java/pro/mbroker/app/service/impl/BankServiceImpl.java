package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.model.bank.Bank;
import pro.mbroker.app.model.bank.BankRepository;
import pro.mbroker.app.model.document.Attachment;
import pro.mbroker.app.model.document.AttachmentRepository;
import pro.mbroker.app.service.BankService;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final AttachmentControllerService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private static final int ORDER_STEP = 10;

    @Override
    public Bank createBank(String name) {
        Bank bank = new Bank()
                .setName(name);
        bank.setOrderNumber(bankRepository.findMaxOrderNumber() + ORDER_STEP);
        return bankRepository.save(bank);
    }

    @Override
    public Bank getBankById(UUID id) {
        return getBank(id);
    }

    @Override
    public void deleteBankById(UUID id) {
        bankRepository.deleteById(id);
    }

    @Override
    public Bank updateBankName(UUID bankId, String name) {
        Bank bank = getBank(bankId);
        bank.setName(name);
        return bankRepository.save(bank);
    }

    @Override
    public MultipartFile getLogoBankById(UUID bankId) {
        Bank bank = getBank(bankId);
        Attachment attachment = attachmentRepository.findById(bank.getLogoAttachmentId())
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bank.getLogoAttachmentId()));
        return download(attachment.getExternalStorageId());
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(Attachment.class, attachmentId));
        return attachmentService.download(attachment.getExternalStorageId());
    }

    @Override
    public Bank updateLogo(UUID bankId, MultipartFile logo) {
        Bank bank = getBank(bankId);
        bank.setLogoAttachmentId(upload(logo));
        return bankRepository.save(bank);
    }

    @Override
    public Long upload(MultipartFile file) {
        AttachmentMeta upload = attachmentService.upload(file);
        Attachment attachment = attachmentRepository.save(new Attachment()
                .setCreatedAt(ZonedDateTime.now())
                .setName(upload.getName())
                .setMimeType(upload.getMimeType())
                .setSizeBytes(upload.getSizeBytes())
                .setContentMd5(upload.getMd5Hash())
                .setExternalStorageId(upload.getId()));
        return attachment.getId();
    }

    @Override
    public List<Bank> getAllBank(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bank> bankPage = bankRepository.findAll(pageable);
        return bankPage.getContent();
    }

    private Bank getBank(UUID bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }
}
