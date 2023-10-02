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
import pro.mbroker.api.dto.request.BankContactRequest;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.response.AttachmentResponse;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.AttachmentMapper;
import pro.mbroker.app.repository.BankContactRepository;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.specification.BankSpecification;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private static final int ORDER_STEP = 10;

    private final BankRepository bankRepository;
    private final AttachmentMapper attachmentMapper;
    private final AttachmentService attachmentService;
    private final BankContactRepository bankContactRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Bank createBank(BankRequest bankRequest) {
        Bank bank = new Bank().setName(bankRequest.getName());
        bank.setContacts(new ArrayList<>());
        setBankContacts(bank, bankRequest.getBankContacts());
        bank.setOrderNumber(bankRepository.findMaxOrderNumber() + ORDER_STEP);
        setAttachmentIfPresent(bank, bankRequest.getAttachment_id());
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public Bank getBankById(UUID id) {
        return getIsActiveBank(id);
    }

    @Override
    @Transactional
    public Bank updateBank(UUID bankId, BankRequest bankRequest) {
        Bank bank = getBank(bankId);
        bank.setName(bankRequest.getName());
        if (Objects.nonNull(bankRequest.getBankContacts())) {
            updateAndAddBankContacts(bank, bankRequest.getBankContacts());
            markAbsentBankContactsAsInactive(bank, bankRequest.getBankContacts());
        }
        setAttachmentIfPresent(bank, bankRequest.getAttachment_id());
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getLogoBankById(UUID bankId) {
        Bank bank = getBank(bankId);
        return attachmentMapper.toAttachmentResponse(
                attachmentService.getAttachmentById(bank.getAttachment().getId()));
    }

    @Override
    @Transactional
    public Bank updateLogo(UUID bankId, MultipartFile logo) {
        Bank bank = getBank(bankId);
        bank.setAttachment(attachmentService.upload(logo));
        return bankRepository.save(bank);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Bank> getAllBank(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Bank> specification = BankSpecification.isActive();
        Page<Bank> bankPage = bankRepository.findAll(specification, pageable);
        List<Bank> banks = bankPage.getContent();
        for (Bank bank : banks) {
            List<BankContact> activeContacts = bank.getContacts()
                    .stream()
                    .filter(BankContact::isActive)
                    .collect(Collectors.toList());
            bank.getContacts().clear();
            bank.getContacts().addAll(activeContacts);
        }
        return banks;
    }

    @Override
    @Transactional
    public void deleteBankById(UUID bankId) {
        Bank bank = getBank(bankId);
        bank.setActive(false);
        bank.getCreditPrograms()
                .forEach(creditProgram -> creditProgram.setActive(false));
        bankRepository.save(bank);
    }

    private void updateAndAddBankContacts(Bank bank, List<BankContactRequest> bankContactRequests) {
        for (BankContactRequest request : bankContactRequests) {
            if (request.getId() != null) {
                BankContact existingContact = bankContactRepository.findById(request.getId())
                        .orElseThrow(() -> new RuntimeException("BankContact not found: " + request.getId()));
                existingContact.setEmail(request.getEmail())
                        .setFullName(request.getFullName());
            } else {
                BankContact newContact = new BankContact()
                        .setEmail(request.getEmail())
                        .setFullName(request.getFullName())
                        .setBank(bank);
                bank.getContacts().add(newContact);
            }
        }
    }

    private void markAbsentBankContactsAsInactive(Bank bank, List<BankContactRequest> bankContactRequests) {
        Set<UUID> requestIds = bankContactRequests.stream()
                .map(BankContactRequest::getId)
                .collect(Collectors.toSet());

        for (BankContact existingContact : bank.getContacts()) {
            if (!requestIds.contains(existingContact.getId())) {
                existingContact.setActive(false);
            }
        }
    }


    private Bank getBank(UUID bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
    }

    private Bank getIsActiveBank(UUID bankId) {
        Specification<Bank> specification = BankSpecification.bankByIdAndIsActive(bankId);
        Bank bank = bankRepository.findOne(specification)
                .orElseThrow(() -> new ItemNotFoundException(Bank.class, bankId));
        bank.setContacts(bank.getContacts().stream().filter(BankContact::isActive).collect(Collectors.toList()));
        bank.setCreditPrograms(bank.getCreditPrograms().stream().filter(CreditProgram::isActive).collect(Collectors.toList()));
        return bank;
    }

    private void setBankContacts(Bank bank, List<BankContactRequest> bankContactRequests) {
        if (Objects.nonNull(bankContactRequests)) {
            List<BankContact> contacts = bankContactRequests.stream()
                    .map(request -> new BankContact().setEmail(request.getEmail())
                            .setFullName(request.getFullName())
                            .setBank(bank))
                    .collect(Collectors.toList());
            bank.getContacts().addAll(contacts);
        }
    }

    private void setAttachmentIfPresent(Bank bank, Long attachmentId) {
        if (Objects.nonNull(attachmentId)) {
            Attachment attachment = attachmentService.getAttachmentById(attachmentId);
            bank.setAttachment(attachment);
        }
    }

    @Transactional
    public void deleteRelationsByBankIdAndEmployerId(UUID bankId, UUID employerId) {
        String sql = "DELETE FROM employer_bank_relation WHERE bank_id = :bankId AND employer_id = :employerId";
        entityManager.createNativeQuery(sql)
                .setParameter("bankId", bankId)
                .setParameter("employerId", employerId)
                .executeUpdate();
    //todo тут происходит какая-то хрень. Транзакция не комитится
    }
}
