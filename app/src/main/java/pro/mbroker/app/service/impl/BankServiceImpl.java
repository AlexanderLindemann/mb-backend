package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.AttachmentRepository;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.specification.BankSpecification;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private static final int ORDER_STEP = 10;

    @Override
    @Transactional
    public Bank createBank(String name) {
        Bank bank = new Bank()
                .setName(name);
        bank.setOrderNumber(bankRepository.findMaxOrderNumber() + ORDER_STEP);
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public Bank getBankById(UUID id) {
        return getIsActiveBank(id);
    }

    @Override
    @Transactional
    public Bank updateBankName(UUID bankId, String name) {
        Bank bank = getBank(bankId);
        bank.setName(name);
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public MultipartFile getLogoBankById(UUID bankId) {
        Bank bank = getBank(bankId);
        Attachment attachment = attachmentRepository.findById(bank.getLogoAttachmentId())
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bank.getLogoAttachmentId()));
        return attachmentService.download(attachment.getExternalStorageId());
    }

    @Override
    @Transactional
    public Bank updateLogo(UUID bankId, MultipartFile logo) {
        Bank bank = getBank(bankId);
        bank.setLogoAttachmentId(attachmentService.upload(logo));
        return bankRepository.save(bank);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Bank> getAllBank(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Bank> specification = BankSpecification.isActive();
        Page<Bank> bankPage = bankRepository.findAll(specification, pageable);
        return bankPage.getContent();
    }

    @Override
    @Transactional
    public void deleteBankById(UUID bankId) {
        Bank bank = getBank(bankId);
        bank.setActive(false);
        bankRepository.save(bank);
    }

    private Bank getBank(UUID bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }

    private Bank getIsActiveBank(UUID bankId) {
        Specification<Bank> specification = BankSpecification.bankByIdAndIsActive(bankId);
        return bankRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }
}
