package pro.mbroker.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.BankContactRequest;
import pro.mbroker.api.dto.BankRequest;
import pro.mbroker.api.dto.BankResponse;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankContactMapper;
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
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankContactRepository bankContactRepository;
    private final BankMapper bankMapper;
    private final BankContactMapper bankContactMapper;
    private final AttachmentControllerService attachmentService;
    private final AttachmentRepository attachmentRepository;

    @Override
    public BankResponse createBank(String name, MultipartFile logoFile) {
        Bank bank = new Bank();
        bank.setName(name);
        bank.setLogo_attachment_id(upload(logoFile));
        Bank savedBank = bankRepository.save(bank);
        return bankMapper.toBankResponseMapper(savedBank);
    }

    @Override
    public BankResponse getBankById(UUID id) {
        Bank bank = getBank(id);
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public BankResponse addBankContact(UUID id, BankContactRequest contactRequest) {
        Bank bank = getBank(id);
        List<BankContact> contacts = bank.getContacts();
        BankContact bankContact = bankContactRepository.save(bankContactMapper
                .toBankContactMapper(contactRequest).setBank(bank));
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
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
        return bankMapper.toBankResponseMapper(bank);
    }

    @Override
    public BankResponse updateBank(UUID id, BankRequest request) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, id));
        if (request.getName() != null) {
            bank.setName(request.getName());
        }
        if (request.getLogoFile() != null) {
            bank.setLogo_attachment_id(upload(request.getLogoFile()));
        }
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

    private Bank getBank(UUID id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, id));
    }
}
