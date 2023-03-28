package pro.mbroker.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankMapper;
import pro.mbroker.app.model.bank.Bank;
import pro.mbroker.app.model.bank.BankContact;
import pro.mbroker.app.model.bank.BankContactRepository;
import pro.mbroker.app.model.bank.BankRepository;
import pro.mbroker.app.model.document.Attachment;
import pro.mbroker.app.model.document.AttachmentRepository;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;
import pro.smartdeal.ng.attachment.api.pojo.AttachmentMeta;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankContactRepository bankContactRepository;
    private final BankMapper bankMapper;
    private final AttachmentControllerService attachmentService;
    private final AttachmentRepository attachmentRepository;

    @Override
    public BankResponse createBank(String name) {
        Bank bank = new Bank()
                .setName(name);
        bank.setOrderNumber(bankRepository.findMaxOrderNumber() + 1);
        Bank savedBank = bankRepository.save(bank);
        return bankMapper.toBankResponseMapper(savedBank);
    }

    @Override
    public BankResponse getBankById(UUID id) {
        Bank bank = getBank(id);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public BankResponse addBankContact(UUID id, String fullName, String email) {
        Bank bank = getBank(id);
        List<BankContact> contacts = bank.getContacts();
        BankContact bankContact = bankContactRepository.save(new BankContact()
                .setBank(bank)
                .setFullName(fullName)
                .setEmail(email));
        contacts.add(bankContact);
        bank.setContacts(contacts);
        bankRepository.save(bank);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public void deleteBankById(UUID id) {
        bankRepository.deleteById(id);
    }

    @Override
    public BankResponse deleteBankContact(UUID contactId) {
        BankContact bankContact = bankContactRepository.findById(contactId)
                .orElseThrow(() -> new ItemNotFoundException(BankContact.class, contactId));
        UUID bankId = bankContact.getBank().getId();
        bankContactRepository.deleteById(contactId);
        Bank bank = getBank(bankId);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public BankResponse updateBankName(UUID bankId, String name) {
        Bank bank = getBank(bankId);
        bank.setName(name);
        bankRepository.save(bank);
        return bankMapper.toBankResponseMapper(bank);
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
    public BankResponse updateLogo(UUID bankId, MultipartFile logo) {
        Bank bank = getBank(bankId);
        bank.setLogoAttachmentId(upload(logo));
        bankRepository.save(bank);
        return bankMapper.toBankResponseMapper(bank);
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
    public List<BankResponse> getAllBank() {
        List<Bank> bankList = bankRepository.findAll();
        if (bankList.isEmpty()) {
            return Collections.emptyList();
        }
        return bankList.stream()
                .map(bankMapper::toBankResponseMapper)
                .collect(Collectors.toList());
    }

    private Bank getBank(UUID bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }
}
