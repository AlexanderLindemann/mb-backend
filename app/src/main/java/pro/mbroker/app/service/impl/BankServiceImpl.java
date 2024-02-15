package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.BankContactRequest;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.FileStorage;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.BankContactRepository;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.specification.BankSpecification;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.FileStorageService;

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
    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;
    private final BankContactRepository bankContactRepository;

    @Override
    @Transactional
    public Bank createBank(BankRequest bankRequest, Integer sdId) {
        Bank bank = new Bank().setName(bankRequest.getName());
        bank.setContacts(new ArrayList<>());
        setBankContacts(bank, bankRequest.getBankContacts(), sdId);
        bank.setOrderNumber(bankRepository.findMaxOrderNumber() + ORDER_STEP);
        bank.setLogoFileStorage(fileStorageService.getFileStorage(bankRequest.getFileStorageId()));
        bank.setCianId(bankRequest.getCianBankId());
        bank.setCreatedBy(sdId);
        bank.setUpdatedBy(sdId);
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public Bank getBankById(UUID id) {
        return getIsActiveBank(id);
    }

    @Override
    @Transactional
    public Bank updateBank(UUID bankId, BankRequest bankRequest, Integer sdId) {
        Bank bank = getBank(bankId);
        bank.setName(bankRequest.getName());
        if (Objects.nonNull(bankRequest.getBankContacts())) {
            updateAndAddBankContacts(bank, bankRequest.getBankContacts(), sdId);
            markAbsentBankContactsAsInactive(bank, bankRequest.getBankContacts(), sdId);
        }
        bank.setCianId(bankRequest.getCianBankId());
        setAttachmentIfPresent(bank, bankRequest.getFileStorageId());
        bank.setUpdatedBy(sdId);
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public FileStorage getLogoBankById(UUID bankId) {
        Bank bank = getBank(bankId);
        return bank.getLogoFileStorage();
    }

    @Override
    @Transactional
    public Bank updateLogo(UUID bankId, UUID fileStorageId, Integer sdId) {
        Bank bank = getBank(bankId);
        bank.setUpdatedBy(sdId);
        setAttachmentIfPresent(bank, fileStorageId);
        return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bank> getAllBank(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Bank> specification = BankSpecification.isActive();
        return bankRepository.findAll(specification, pageable)
                .map(bank -> {
                    List<BankContact> activeContacts = bank.getContacts()
                            .stream()
                            .filter(BankContact::isActive)
                            .collect(Collectors.toList());
                    bank.setContacts(activeContacts);
                    return bank;
                })
                .getContent();
    }

    @Override
    public List<Bank> getAllBankByIds(Set<UUID> bankIds) {
        List<Bank> banks = bankRepository.findAllByIdInAndIsActiveTrue(bankIds);
        Set<UUID> foundBankIds = banks.stream()
                .map(Bank::getId)
                .collect(Collectors.toSet());
        bankIds.removeAll(foundBankIds);
        if (!bankIds.isEmpty()) {
            throw new ItemNotFoundException(Bank.class, " with the following IDs were not found or are not active: " + bankIds);
        }
        return banks;
    }

    @Override
    public Bank findBankByCianId(Integer cianId) {
        return bankRepository.findBankByCianId(cianId);
    }

    @Override
    @Transactional
    public void deleteBankById(UUID bankId, Integer sdId) {
        Bank bank = getBank(bankId);
        bank.setActive(false);
        bank.setUpdatedBy(sdId);
        bank.getContacts().forEach(bankContact -> bankContact.setActive(false).setUpdatedBy(sdId));
        bank.getCreditPrograms().forEach(creditProgram -> creditProgram.setActive(false).setUpdatedBy(sdId));
        bankRepository.save(bank);
    }

    private void updateAndAddBankContacts(Bank bank, List<BankContactRequest> bankContactRequests, Integer sdId) {
        for (BankContactRequest request : bankContactRequests) {
            if (request.getId() != null) {
                BankContact existingContact = bankContactRepository.findById(request.getId())
                        .orElseThrow(() -> new RuntimeException("BankContact not found: " + request.getId()));
                existingContact.setEmail(request.getEmail())
                        .setFullName(request.getFullName())
                        .setUpdatedBy(sdId);
            } else {
                BankContact newContact = new BankContact()
                        .setEmail(request.getEmail())
                        .setFullName(request.getFullName())
                        .setBank(bank);
                newContact.setCreatedBy(sdId);
                bank.getContacts().add(newContact);
            }
        }
    }

    private void markAbsentBankContactsAsInactive(Bank bank, List<BankContactRequest> bankContactRequests, Integer sdId) {
        Set<UUID> requestIds = bankContactRequests.stream()
                .map(BankContactRequest::getId)
                .collect(Collectors.toSet());
        for (BankContact existingContact : bank.getContacts()) {
            if (!requestIds.contains(existingContact.getId())) {
                existingContact.setActive(false)
                        .setUpdatedBy(sdId);
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

    private void setBankContacts(Bank bank, List<BankContactRequest> bankContactRequests, Integer sdId) {
        if (Objects.nonNull(bankContactRequests)) {
            List<BankContact> contacts = bankContactRequests.stream()
                    .map(request -> {
                        BankContact bankContact = new BankContact()
                                .setEmail(request.getEmail())
                                .setFullName(request.getFullName())
                                .setBank(bank);
                        bankContact.setCreatedBy(sdId);
                        return bankContact;
                    })
                    .collect(Collectors.toList());
            bank.getContacts().addAll(contacts);
        }
    }

    private void setAttachmentIfPresent(Bank bank, UUID fileStorageId) {
        if (Objects.nonNull(fileStorageId)) {
            FileStorage fileStorage = fileStorageService.getFileStorage(fileStorageId);
            bank.setLogoFileStorage(fileStorage);
        }
    }
}
