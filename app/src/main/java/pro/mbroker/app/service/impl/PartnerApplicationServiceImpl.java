package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.BankApplicationKey;
import pro.mbroker.api.dto.BankWithBankApplicationDto;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.api.dto.SalaryClientProgramCalculationDto;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerApplicationServiceRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PaymentSource;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BaseEntity;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.MortgageCalculation;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.entity.RealEstate;
import pro.mbroker.app.exception.AccessDeniedException;
import pro.mbroker.app.exception.BadRequestException;
import pro.mbroker.app.exception.ItemConflictException;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.mapper.BankApplicationMapper;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.mapper.MortgageCalculationMapper;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.repository.BankRepository;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.repository.PartnerApplicationRepository;
import pro.mbroker.app.repository.specification.PartnerApplicationSpecification;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.CalculatorService;
import pro.mbroker.app.service.CreditProgramService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.service.RealEstateService;
import pro.mbroker.app.service.StatusService;
import pro.mbroker.app.util.Converter;
import pro.mbroker.app.util.Pagination;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationServiceImpl implements PartnerApplicationService {

    private final CalculatorService calculatorService;
    private final AttachmentService attachmentService;
    private final PartnerService partnerService;
    private final RealEstateService realEstateService;
    private final CreditProgramService creditProgramService;
    private final StatusService statusService;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final BankRepository bankRepository;
    private final PartnerApplicationMapper partnerApplicationMapper;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final BankApplicationMapper bankApplicationMapper;
    private final MortgageCalculationMapper mortgageCalculationMapper;

    private static final Set<BankApplicationStatus> UNCHANGEABLE_STATUSES = Set.of(
            BankApplicationStatus.SENT_TO_BANK,
            BankApplicationStatus.SENDING_TO_BANK,
            BankApplicationStatus.APPLICATION_APPROVED,
            BankApplicationStatus.CREDIT_APPROVED,
            BankApplicationStatus.REFINEMENT,
            BankApplicationStatus.REJECTED,
            BankApplicationStatus.SENDING_ERROR,
            BankApplicationStatus.EXPIRED
    );

    @Override
    @Transactional(readOnly = true)
    public Page<PartnerApplication> getAllPartnerApplication(PartnerApplicationServiceRequest request) {
        log.info("Getting all partner applications with pagination: page={}, size={}, sortBy={}, sortOrder={}", request.getPage(), request.getSize(), request.getSortBy(), request.getSortOrder());
        Pageable pageable = Pagination.createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortOrder());
        Specification<PartnerApplication> specification = PartnerApplicationSpecification.buildSearchSpecification(
                request.getStartDate(),
                request.getEndDate(),
                request.getPhoneNumber(),
                request.getFullName(),
                request.getApplicationNumber(),
                request.getRealEstateId(),
                request.getRegion(),
                request.getBankId(),
                request.getApplicationStatus(),
                request.getIsActive());
        if (request.getPermissions().contains("MB_ADMIN_ACCESS")) {
            return fetchPartnerApplications(specification, pageable);
        } else if (request.getPermissions().contains("MB_REQUEST_READ_ORGANIZATION")) {
            List<UUID> partnerIds = partnerService.getCurrentPartners(request.getOrganisationId()).stream()
                    .map(Partner::getId)
                    .collect(Collectors.toList());
            return fetchPartnerApplications(specification.and(PartnerApplicationSpecification.partnerIdIn(partnerIds)), pageable);
        } else if (request.getPermissions().contains("MB_REQUEST_READ_OWN")) {
            return fetchPartnerApplications(specification.and(PartnerApplicationSpecification.createdByEquals(request.getSdId())), pageable);
        } else if (request.getPermissions().contains("SD_MOBILE_INTERACTION")) {
            String tokenPhoneNumber = formatPhoneNumber(request.getTokenPhoneNumber());
            List<PartnerApplication> partnerApplications = getPartnerApplicationsByPhoneNumber(tokenPhoneNumber);
            return new PageImpl<>(getPartnerApplicationsByPhoneNumber(tokenPhoneNumber), pageable, partnerApplications.size());
        } else {
            log.warn("User does not have any valid permission to fetch partner applications");
            return Page.empty(pageable);
        }
    }

    private Page<PartnerApplication> fetchPartnerApplications(Specification<PartnerApplication> specification, Pageable pageable) {
        try {
            Page<PartnerApplication> result = partnerApplicationRepository.findAll(specification, pageable);
            return result.map(this::sortBorrowerProfilesInApplication);
        } catch (Exception e) {
            log.error("Error while fetching partner applications", e);
            return Page.empty(pageable);
        }
    }

    private PartnerApplication sortBorrowerProfilesInApplication(PartnerApplication partnerApplication) {
        List<BorrowerProfile> sortedBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .sorted(Comparator.comparing(BorrowerProfile::getCreatedAt))
                .collect(Collectors.toList());
        partnerApplication.setBorrowerProfiles(sortedBorrowerProfiles);
        return partnerApplication;
    }

    @Override
    @Transactional
    public PartnerApplication changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId, Integer sdId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        BorrowerProfile borrowerProfile = borrowerProfileRepository.findById(newMainBorrowerId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, newMainBorrowerId));
        partnerApplication.getBankApplications()
                .forEach(bankApplication -> {
                    if (!UNCHANGEABLE_STATUSES.contains(bankApplication.getBankApplicationStatus()))
                        bankApplication.setMainBorrower(borrowerProfile);
                    bankApplication.setUpdatedBy(sdId);
                });
        partnerApplication.setUpdatedBy(sdId);
        return save(partnerApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerApplication getPartnerApplicationByAttachmentId(Long attachmentId) {
        return partnerApplicationRepository.findByAttachmentId(attachmentId)
                .orElseThrow(() -> new AccessDeniedException(attachmentId, PartnerApplication.class));
    }

    @Override
    @Transactional
    public PartnerApplication createPartnerApplication(PartnerApplicationRequest request, Integer sdId) {
        PartnerApplication partnerApplication = getPartnerApplication(request, sdId);
        List<BankApplication> bankApplications = buildBankApplications(request, partnerApplication, sdId);
        partnerApplication.setBankApplications(bankApplications);
        updateMainBorrower(partnerApplication, request.getMainBorrower(), sdId);
        if (Objects.nonNull(request.getExternalCreatorId())) {
            partnerApplication.setExternalCreatorId(request.getExternalCreatorId());
        }
        statusService.statusChanger(partnerApplication);
        return save(partnerApplication);
    }

    //TODO переделать на подобии обновления BorrowerProfile
    @Override
    @Transactional
    public PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request, Integer sdId) {
        PartnerApplication partnerApplication = getPartnerApplicationByIdCheckPermission(partnerApplicationId);
        boolean isChanged = false;
        if (isBorrowerProfileChanged(request, partnerApplication)) {
            deleteBorrowerDocuments(partnerApplication, sdId);
            isChanged = true;
        }
        if (request.getMortgageCalculation() != null) {
            if (partnerApplication.getMortgageCalculation() == null) {
                partnerApplication.setMortgageCalculation(new MortgageCalculation());
            }
            mortgageCalculationMapper.updateMortgageCalculationFromRequest(request.getMortgageCalculation(), partnerApplication.getMortgageCalculation());
            isChanged = true;
        }
        partnerApplicationMapper.updatePartnerApplicationFromRequest(request, partnerApplication);
        if (Objects.nonNull(request.getPaymentSource())) {
            String requestPaymentSource = Converter.convertEnumListToString(request.getPaymentSource());
            String existPaymentSource = partnerApplication.getPaymentSource();
            if (existPaymentSource != null
                    && !existPaymentSource.equals(requestPaymentSource) ||
                    existPaymentSource == null && requestPaymentSource != null) {
                partnerApplication.setPaymentSource(requestPaymentSource);
                isChanged = true;
            }
        }
        if (Objects.isNull(request.getPaymentSource()) && Objects.nonNull(partnerApplication.getPaymentSource())) {
            partnerApplication.setPaymentSource(null);
            isChanged = true;
        }
        if (Objects.nonNull(request.getInsurances())) {
            String requestInsurances = Converter.convertEnumListToString(request.getInsurances());
            String existInsurances = partnerApplication.getInsurances();
            if (existInsurances != null
                    && !existInsurances.equals(requestInsurances) ||
                    existInsurances == null && requestInsurances != null) {
                partnerApplication.setInsurances(requestInsurances);
                isChanged = true;
            }
        }
        if (Objects.isNull(request.getInsurances()) && Objects.nonNull(partnerApplication.getInsurances())) {
            partnerApplication.setInsurances(null);
            isChanged = true;
        }
        setSalaryBank(request, partnerApplication);
        if (Objects.nonNull(request.getRealEstateId())) {
            if (partnerApplication.getRealEstate() != null
                    && !partnerApplication.getRealEstate().getId().toString().equals(request.getRealEstateId())) {
                partnerApplication.setRealEstate(realEstateService.findByRealEstateId(request.getRealEstateId()));
                isChanged = true;
            }
        }
        if (Objects.nonNull(request.getBankApplications())) {
            List<BankApplication> updatedBorrowerApplications = buildBankApplications(request, partnerApplication, sdId);
            List<BankApplication> existBankApplication = partnerApplication.getBankApplications();

            if (existBankApplication != null
                    && !new HashSet<>(existBankApplication).containsAll(updatedBorrowerApplications)) {
                partnerApplication.setBankApplications(updatedBorrowerApplications);
                isChanged = true;
            }
        }
        if (Objects.nonNull(request.getRealEstateTypes())) {
            partnerApplication.setRealEstateTypes(Converter.convertEnumListToString(request.getRealEstateTypes()));
        }
        statusService.statusChanger(partnerApplication);
        return isChanged ? save(partnerApplication) : partnerApplication;
    }

    @Override
    public PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication) {
        PartnerApplicationResponse response = partnerApplicationMapper.toPartnerApplicationResponse(partnerApplication);
        response.setRealEstateTypes(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class));
        response.setInsurances(Converter.convertStringListToEnumList(partnerApplication.getInsurances(), Insurance.class));
        Map<UUID, BorrowerProfile> borrowerProfileMap = getActiveBorrowerProfilesMap(partnerApplication);
        List<BankApplicationResponse> activeBankApplicationResponses = getActiveBankApplicationResponses(partnerApplication, borrowerProfileMap);
        List<BankWithBankApplicationDto> bankWithBankApplicationDtos = getGroupBankApplication(activeBankApplicationResponses);
        response.setBankWithBankApplicationDto(bankWithBankApplicationDtos);
        response.setPaymentSource(Converter.convertStringListToEnumList(partnerApplication.getPaymentSource(), PaymentSource.class));
        response.setBorrowerProfiles(
                borrowerProfileMap.values()
                        .stream()
                        .map(borrowerProfileMapper::toBorrowerProfileResponse)
                        .sorted(Comparator.comparing(BorrowerProfileResponse::getCreatedAt)) // Сортировка по дате создания
                        .collect(Collectors.toList())
        );
        return response;
    }

    @Override
    public List<PartnerApplicationResponse> buildPartnerApplicationResponse(List<PartnerApplication> partnerApplications) {
        Map<UUID, PartnerApplicationResponse> partnerResponseMap = partnerApplications.stream()
                .map(partnerApplicationMapper::toPartnerApplicationResponse)
                .collect(Collectors.toMap(PartnerApplicationResponse::getId, Function.identity()));
        Set<UUID> creditProgramIds = partnerApplications.stream()
                .flatMap(pa -> pa.getBankApplications().stream())
                .map(BankApplication::getCreditProgram)
                .map(CreditProgram::getId)
                .collect(Collectors.toSet());
        Map<UUID, Bank> bankMap = creditProgramService.getProgramByCreditProgramIds(new ArrayList<>(creditProgramIds)).stream()
                .collect(Collectors.toMap(CreditProgram::getId, CreditProgram::getBank));
        for (PartnerApplication partnerApplication : partnerApplications) {
            UUID id = partnerApplication.getId();
            PartnerApplicationResponse response = partnerResponseMap.get(id);
            response.setRealEstateTypes(Converter.convertStringListToEnumList(partnerApplication.getRealEstateTypes(), RealEstateType.class));
            Map<UUID, BorrowerProfile> borrowerProfileMap = getActiveBorrowerProfilesMap(partnerApplication);
            List<BankApplicationResponse> activeBankApplicationResponses = getActiveBankApplicationResponses(partnerApplication, borrowerProfileMap);
            Set<Bank> banks = new HashSet<>();
            for (BankApplication bankApplication : partnerApplication.getBankApplications()) {
                Bank bank = bankMap.get(bankApplication.getCreditProgram().getId());
                banks.add(bank);
            }
            List<BankWithBankApplicationDto> bankWithBankApplicationDtos = getGroupBankApplication(activeBankApplicationResponses, banks);
            response.setBankWithBankApplicationDto(bankWithBankApplicationDtos);
            response.setPaymentSource(Converter.convertStringListToEnumList(partnerApplication.getPaymentSource(), PaymentSource.class));
            response.setBorrowerProfiles(
                    borrowerProfileMap.values()
                            .stream()
                            .map(borrowerProfileMapper::toBorrowerProfileResponse)
                            .sorted(Comparator.comparing(BorrowerProfileResponse::getCreatedAt)) // Сортировка по дате создания
                            .collect(Collectors.toList()));
        }
        return new ArrayList<>(partnerResponseMap.values());
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerApplication getPartnerApplicationByIdCheckPermission(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        List<BorrowerProfile> sortedBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .sorted(Comparator.comparing(BorrowerProfile::getCreatedAt))
                .collect(Collectors.toList());
        partnerApplication.setBorrowerProfiles(sortedBorrowerProfiles);
        return partnerApplication;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerApplication getPartnerApplication(UUID partnerApplicationId) {
        return partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, partnerApplicationId));
    }

    @Override
    @Transactional
    public PartnerApplication enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request, Integer sdId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        BankApplication currentBankApplication = currentBankApplications.get(request.getCreditProgramId());
        if (currentBankApplication != null) {
            bankApplicationMapper.updateBankApplicationFromRequest(currentBankApplication, request);
            currentBankApplication.setUpdatedBy(sdId);
        } else {
            BankApplication newBankApplication = bankApplicationMapper.toBankApplication(request);
            newBankApplication.setPartnerApplication(partnerApplication);
            newBankApplication.setMainBorrower(getBorrowerProfile(request.getMainBorrowerId()));
            newBankApplication.setCreatedBy(sdId);
            newBankApplication.setUpdatedBy(sdId);
            currentBankApplications.put(request.getCreditProgramId(), newBankApplication);
        }
        partnerApplication.setBankApplications(new ArrayList<>(currentBankApplications.values()));
        return save(partnerApplication);
    }

    @Override
    @Transactional
    public PartnerApplication disableBankApplication(UUID partnerApplicationId, UUID creditProgramId, Integer sdId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        BankApplication disabledBankApplication = currentBankApplications.get(creditProgramId);
        if (disabledBankApplication == null) {
            throw new ItemConflictException(CreditProgram.class, " This PartnerApplication with " + partnerApplicationId + " does not have such a credit program id: " + creditProgramId);
        }
        disabledBankApplication.setActive(false);
        disabledBankApplication.setUpdatedBy(sdId);
        partnerApplication.setBankApplications(new ArrayList<>(currentBankApplications.values()));
        partnerApplication.setUpdatedBy(sdId);
        return save(partnerApplication);
    }

    @Override
    @Transactional
    public List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        List<Bank> banks = partnerApplication.getBankApplications().stream()
                .map(BankApplication::getCreditProgram)
                .map(CreditProgram::getBank)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<RequiredDocumentResponse> response = banks.stream()
                .flatMap(bank -> Stream.of(DocumentType.APPLICATION_FORM, DocumentType.DATA_PROCESSING_AGREEMENT)
                        .map(documentType -> new RequiredDocumentResponse()
                                .setBankId(bank.getId())
                                .setDocumentType(documentType)
                                .setBankName(bank.getName())))
                .collect(Collectors.toList());
        Stream.of(DocumentType.BORROWER_SNILS,
                        DocumentType.BORROWER_PASSPORT,
                        DocumentType.INCOME_CERTIFICATE,
                        DocumentType.CERTIFIED_COPY_TK)
                .map(documentType -> new RequiredDocumentResponse().setDocumentType(documentType))
                .forEach(response::add);
        return response;
    }

    @Override
    public List<PartnerApplication> findPartnerApplicationByPhoneNumber(String phoneNumber) {
        return partnerApplicationRepository.findByBorrowerPhoneNumber(phoneNumber);
    }

    @Override
    public List<PartnerApplication> getPartnerApplicationByIds(List<UUID> ids) {
        return partnerApplicationRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public void saveAll(Collection<PartnerApplication> partnerApplications) {
        partnerApplicationRepository.saveAll(partnerApplications);
    }

    @Override
    public PartnerApplication save(PartnerApplication partnerApplication) {
        partnerApplication.setUpdatedAt(LocalDateTime.now());
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    public void changePartnerApplicationActiveStatus(UUID partnerApplicationId, boolean isActive, Integer sdId) {
        PartnerApplication partnerApplication = getPartnerApplicationById(partnerApplicationId);
        partnerApplication.setActive(isActive);
        partnerApplication.setUpdatedBy(sdId);
        save(partnerApplication);
    }

    private String formatPhoneNumber(String phoneNumber) {
        String cleanNumber = phoneNumber.startsWith("+") ? phoneNumber.substring(1) : phoneNumber;
        if (cleanNumber.length() > 10) {
            return cleanNumber.substring(cleanNumber.length() - 10);
        } else {
            return cleanNumber;
        }
    }

    private List<PartnerApplication> getPartnerApplicationsByPhoneNumber(String phoneNumber) {
        List<BorrowerProfile> borrowerProfiles = borrowerProfileRepository.findAllByPhoneNumberAndIsActiveTrue(phoneNumber);
        List<PartnerApplication> partnerApplications = borrowerProfiles.stream()
                .map(BorrowerProfile::getPartnerApplication)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        partnerApplications.forEach(partnerApplication -> {
            if (partnerApplication.getBorrowerProfiles() != null) {
                List<BorrowerProfile> profiles = partnerApplication.getBorrowerProfiles().stream()
                        .filter(borrowerProfile ->
                                borrowerProfile != null &&
                                        borrowerProfile.getPhoneNumber() != null &&
                                        borrowerProfile.getPhoneNumber().equals(phoneNumber) &&
                                        borrowerProfile.isActive())
                        .collect(Collectors.toList());
                partnerApplication.setBorrowerProfiles(profiles);
            }
        });
        return partnerApplications;
    }

    private void setSalaryBank(PartnerApplicationRequest request, PartnerApplication partnerApplication) {
        if (Objects.nonNull(request.getMortgageCalculation()) && Objects.nonNull(request.getMortgageCalculation().getSalaryBanks())) {
            List<Bank> banks = bankRepository.findAllById(request.getMortgageCalculation().getSalaryBanks());
            List<Bank> existBanks = partnerApplication.getMortgageCalculation().getSalaryBanks();

            if (existBanks != null
                    && !new HashSet<>(existBanks).containsAll(banks)) {
                partnerApplication.getMortgageCalculation().setSalaryBanks(banks);
            }
        }
    }

    private boolean isBorrowerProfileChanged(PartnerApplicationRequest request, PartnerApplication existingApplication) {
        if (Objects.nonNull(request.getMaternalCapitalAmount())
                && !Objects.equals(request.getMaternalCapitalAmount(), existingApplication.getMaternalCapitalAmount())) {
            return true;
        }
        if (Objects.nonNull(request.getSubsidyAmount())
                && !Objects.equals(request.getSubsidyAmount(), existingApplication.getSubsidyAmount())) {
            return true;
        }
        List<PaymentSource> existingPaymentSources = Converter.convertStringListToEnumList(existingApplication.getPaymentSource(), PaymentSource.class);
        if (Objects.nonNull(request.getPaymentSource())) {
            if (request.getPaymentSource().size() != existingPaymentSources.size()) {
                return true;
            } else {
                if (!Objects.equals(existingPaymentSources, request.getPaymentSource())) {
                    return true;
                }
            }
        }
        if (Objects.nonNull(request.getInsurances())
                && Objects.nonNull(existingApplication.getInsurances())
                && !Objects.equals(Converter.convertEnumListToString(request.getInsurances()), existingApplication.getInsurances())) {
            return true;
        }
        if (Objects.nonNull(request.getCreditPurposeType())
                && Objects.nonNull(existingApplication.getCreditPurposeType())
                && !Objects.equals(request.getCreditPurposeType(), existingApplication.getCreditPurposeType())) {
            return true;
        }
        if (Objects.nonNull(request.getRealEstateTypes())
                && Objects.nonNull(existingApplication.getRealEstateTypes())
                && !Objects.equals(request.getRealEstateTypes(), Converter.convertStringListToEnumList(existingApplication.getRealEstateTypes(), RealEstateType.class))) {
            return true;
        }
        UUID existingRealEstateId = existingApplication.getRealEstate() != null ? existingApplication.getRealEstate().getId() : null;
        if (Objects.nonNull(existingRealEstateId)
                && Objects.nonNull(request.getRealEstateId())
                && !Objects.equals(request.getRealEstateId(), existingRealEstateId.toString())) {
            return true;
        }
        MortgageCalculationDto requestMortgageCalculation = request.getMortgageCalculation();
        MortgageCalculation existingMortgageCalculation = existingApplication.getMortgageCalculation();
        if (Objects.nonNull(requestMortgageCalculation) && Objects.nonNull(existingMortgageCalculation)) {
            if (!Objects.equals(requestMortgageCalculation.getRealEstatePrice(), existingMortgageCalculation.getRealEstatePrice())) {
                return true;
            }
            if (!Objects.equals(requestMortgageCalculation.getDownPayment(), existingMortgageCalculation.getDownPayment())) {
                return true;
            }
            if (!Objects.equals(requestMortgageCalculation.getIsMaternalCapital(), existingMortgageCalculation.getIsMaternalCapital())) {
                return true;
            }
            return !Objects.equals(requestMortgageCalculation.getCreditTerm(), existingMortgageCalculation.getMonthCreditTerm() / 12);
        }
        return false;
    }


    private void deleteBorrowerDocuments(PartnerApplication application, Integer sdId) {
        EnumSet<DocumentType> typesToDelete = EnumSet.of(
                DocumentType.APPLICATION_FORM,
                DocumentType.GENERATED_FORM,
                DocumentType.SIGNATURE_FORM,
                DocumentType.GENERATED_SIGNATURE_FORM);

        application.getBorrowerProfiles().forEach(borrowerProfile -> {
            List<BorrowerDocument> documents = borrowerProfile.getBorrowerDocument();
            documents.stream()
                    .filter(document -> typesToDelete.contains(document.getDocumentType()))
                    .forEach(document -> {
                        document.setActive(false);
                        document.setUpdatedBy(sdId);
                    });
        });
    }

    private Map<UUID, BorrowerProfile> getActiveBorrowerProfilesMap(PartnerApplication partnerApplication) {
        return partnerApplication.getBorrowerProfiles().stream()
                .filter(BorrowerProfile::isActive)
                .collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
    }

    private List<BankApplicationResponse> getActiveBankApplicationResponses(PartnerApplication partnerApplication, Map<UUID, BorrowerProfile> borrowerProfileMap) {
        return partnerApplication.getBankApplications().stream()
                .filter(BankApplication::isActive)
                .map(bankApplication -> getBankApplicationResponse(partnerApplication, bankApplication, borrowerProfileMap))
                .collect(Collectors.toList());
    }

    private BankApplicationResponse getBankApplicationResponse(PartnerApplication partnerApplication, BankApplication bankApplication, Map<UUID, BorrowerProfile> borrowerProfileMap) {
        BankApplicationResponse bankApplicationResponse = bankApplicationMapper.toBankApplicationResponse(bankApplication);
        BigDecimal mortgageSum = calculatorService.getMortgageSum(bankApplication.getRealEstatePrice(), bankApplication.getDownPayment());
        setSalaryApplicationPropertiesIfApplicable(partnerApplication, bankApplication, bankApplicationResponse, mortgageSum);
        bankApplicationResponse.setMortgageSum(mortgageSum);
        setCoBorrowers(bankApplication, bankApplicationResponse, borrowerProfileMap);
        return bankApplicationResponse;
    }

    private void setSalaryApplicationPropertiesIfApplicable(PartnerApplication partnerApplication, BankApplication bankApplication, BankApplicationResponse bankApplicationResponse, BigDecimal mortgageSum) {
        if (Objects.nonNull(partnerApplication.getMortgageCalculation())
                && Objects.nonNull(partnerApplication.getMortgageCalculation().getSalaryBanks())
                && partnerApplication.getMortgageCalculation().getSalaryBanks()
                .contains(bankApplication.getCreditProgram().getBank())) {
            CreditProgram creditProgram = bankApplication.getCreditProgram();
            Optional.ofNullable(creditProgram.getSalaryClientInterestRate())
                    .ifPresent(salaryClientInterestRate -> {
                        double calculateBaseRate = creditProgram.getBaseRate() + salaryClientInterestRate;
                        BigDecimal monthlyPayment = calculatorService.calculateMonthlyPayment(mortgageSum, calculateBaseRate, bankApplication.getMonthCreditTerm());
                        bankApplicationResponse.setSalaryClientCalculation(new SalaryClientProgramCalculationDto()
                                .setMonthlyPayment(monthlyPayment)
                                .setSalaryBankRate(creditProgram.getSalaryClientInterestRate())
                                .setOverpayment(calculatorService.calculateOverpayment(monthlyPayment, bankApplication.getMonthCreditTerm(), bankApplication.getRealEstatePrice(), bankApplication.getDownPayment()))
                                .setCalculatedRate(calculateBaseRate));
                    });
        }
    }

    private void setCoBorrowers(BankApplication bankApplication, BankApplicationResponse bankApplicationResponse, Map<UUID, BorrowerProfile> borrowerProfileMap) {
        BorrowerProfile mainBorrower = bankApplication.getMainBorrower();
        UUID mainBorrowerId = mainBorrower != null ? mainBorrower.getId() : null;
        List<BorrowerProfileResponse> coBorrowers = borrowerProfileMap.values().stream()
                .filter(borrowerProfile -> mainBorrower == null || !borrowerProfile.getId().equals(mainBorrowerId))
                .map(borrowerProfileMapper::toBorrowerProfileResponse)
                .collect(Collectors.toList());
        bankApplicationResponse.setCoBorrowers(coBorrowers);
    }

    private List<BankWithBankApplicationDto> getGroupBankApplication(List<BankApplicationResponse> activeBankApplicationResponses, Set<Bank> banks) {
        Map<UUID, List<BankApplicationResponse>> bankApplicationMap = activeBankApplicationResponses.stream()
                .collect(Collectors.groupingBy(BankApplicationResponse::getCreditProgramId));
        return banks.stream()
                .map(bank -> createBankWithBankApplicationDto(bank, bankApplicationMap))
                .peek(dto -> dto.getBankApplications().sort(Comparator.comparing(BankApplicationResponse::getMonthlyPayment)))
                .sorted(Comparator.comparing(bankWithBankApplicationDto ->
                        bankWithBankApplicationDto.getBankApplications().stream()
                                .min(Comparator.comparing(BankApplicationResponse::getMonthlyPayment))
                                .map(BankApplicationResponse::getMonthlyPayment)
                                .orElse(BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

    private List<BankWithBankApplicationDto> getGroupBankApplication(List<BankApplicationResponse> activeBankApplicationResponses) {
        Map<UUID, List<BankApplicationResponse>> bankApplicationMap = activeBankApplicationResponses.stream()
                .collect(Collectors.groupingBy(BankApplicationResponse::getCreditProgramId));
        Set<Bank> banks = creditProgramService.getProgramByCreditProgramIds(new ArrayList<>(bankApplicationMap.keySet()))
                .stream()
                .map(CreditProgram::getBank)
                .collect(Collectors.toSet());
        return banks.stream()
                .map(bank -> createBankWithBankApplicationDto(bank, bankApplicationMap))
                .peek(dto -> dto.getBankApplications().sort(Comparator.comparing(BankApplicationResponse::getMonthlyPayment)))
                .sorted(Comparator.comparing(bankWithBankApplicationDto ->
                        bankWithBankApplicationDto.getBankApplications().stream()
                                .min(Comparator.comparing(BankApplicationResponse::getMonthlyPayment))
                                .map(BankApplicationResponse::getMonthlyPayment)
                                .orElse(BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

    private BankWithBankApplicationDto createBankWithBankApplicationDto(Bank bank, Map<UUID, List<BankApplicationResponse>> bankApplicationMap) {
        BankWithBankApplicationDto dto = new BankWithBankApplicationDto();
        dto.setBankName(bank.getName());
        dto.setBankId(bank.getId());
        if (Objects.nonNull(bank.getLogoFileStorage()) && Objects.nonNull(bank.getLogoFileStorage().getId()))
            dto.setLogo(attachmentService.getSignedUrl(bank.getLogoFileStorage().getObjectKey()));
        List<UUID> creditProgramIds = bank.getCreditPrograms().stream()
                .filter(BaseEntity::isActive)
                .map(CreditProgram::getId)
                .collect(Collectors.toList());
        List<BankApplicationResponse> applications = creditProgramIds.stream()
                .flatMap(id -> bankApplicationMap.getOrDefault(id, Collections.emptyList()).stream())
                .collect(Collectors.toList());
        dto.setBankApplications(applications);
        return dto;
    }

    private void updateMainBorrower(PartnerApplication partnerApplication, BorrowerProfileRequest borrowerProfileRequest, Integer sdId) {
        validateMainBorrower(borrowerProfileRequest);
        BorrowerProfile borrowerProfile;
        if (borrowerProfileRequest.getId() != null) {
            borrowerProfile = getBorrowerProfile(borrowerProfileRequest.getId());
            borrowerProfile.setUpdatedBy(sdId);
            borrowerProfileMapper.updateBorrowerProfile(borrowerProfileRequest, borrowerProfile);
        } else {
            if (!partnerApplication.getBorrowerProfiles().isEmpty()) {
                throw new ItemConflictException(BorrowerProfile.class,
                        "The PartnerApplication id: " + partnerApplication.getId() + " already has a mainBorrower. You need to specify the ID of the existing mainBorrower in the request");
            }
            borrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrowerProfileRequest);
            borrowerProfile.setCreatedBy(sdId);
            borrowerProfile.setUpdatedBy(sdId);
            partnerApplication.getBorrowerProfiles().add(borrowerProfile);
        }
        borrowerProfile.setPartnerApplication(partnerApplication);
        partnerApplication.getBankApplications().forEach(app -> app.setMainBorrower(borrowerProfile));
    }

    private void validateMainBorrower(BorrowerProfileRequest borrowerProfileRequest) {
        if (borrowerProfileRequest == null) {
            throw new BadRequestException("A main borrower is required for creating an application.");
        }
        StringBuilder missingFields = new StringBuilder();
        if (borrowerProfileRequest.getFirstName() == null) {
            missingFields.append("first name, ");
        }
        if (borrowerProfileRequest.getLastName() == null) {
            missingFields.append("last name, ");
        }
        if (borrowerProfileRequest.getPhoneNumber() == null) {
            missingFields.append("phone number, ");
        }
        if (missingFields.length() > 0) {
            missingFields.delete(missingFields.length() - 2, missingFields.length());
            throw new BadRequestException("The following fields are missing for the main borrower: " + missingFields);
        }
    }


    private BorrowerProfile getBorrowerProfile(UUID borrowerProfileId) {
        return borrowerProfileRepository.findById(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    private List<BankApplication> buildBankApplications(PartnerApplicationRequest requests, PartnerApplication partnerApplication, Integer sdId) {
        Optional<BorrowerProfileRequest> optionalBorrower = Optional.ofNullable(requests.getMainBorrower());
        BorrowerProfile mainBorrower = optionalBorrower
                .filter(borrower -> Objects.nonNull(borrower.getId()))
                .map(borrower -> getBorrowerProfile(borrower.getId()))
                .orElseGet(() -> borrowerProfileMapper.toBorrowerProfile(optionalBorrower.orElse(null)));
        List<BankApplicationRequest> bankApplicationRequests = requests.getBankApplications();
        List<BankApplicationKey> updateCreditProgramIds = bankApplicationRequests.stream()
                .map(request -> new BankApplicationKey(request.getCreditProgramId(), request.getRealEstateType()))
                .collect(Collectors.toList());
        MortgageCalculation mortgageCalculation = partnerApplication.getMortgageCalculation();
        if (Objects.nonNull(mortgageCalculation) && mortgageCalculation.getIsMaternalCapital() == null) {
            mortgageCalculation.setIsMaternalCapital(false);
        }
        Map<BankApplicationKey, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .filter(ba -> !UNCHANGEABLE_STATUSES.contains(ba.getBankApplicationStatus()))
                .collect(Collectors.toMap(
                        bankApplication -> new BankApplicationKey(bankApplication.getCreditProgram().getId(), bankApplication.getRealEstateType()),
                        Function.identity()
                ));
        bankApplicationRequests.forEach(bankApplicationRequest -> {
            BankApplication currentBankApplication = currentBankApplications.get(new BankApplicationKey(bankApplicationRequest.getCreditProgramId(),
                    bankApplicationRequest.getRealEstateType()));
            if (currentBankApplication != null) {
                bankApplicationMapper.updateBankApplicationFromRequest(currentBankApplication, bankApplicationRequest, sdId);
            } else {
                BankApplication newBankApplication = bankApplicationMapper.toBankApplication(bankApplicationRequest)
                        .setMainBorrower(mainBorrower)
                        .setPartnerApplication(partnerApplication);
                currentBankApplications.put(new BankApplicationKey(bankApplicationRequest.getCreditProgramId(),
                                bankApplicationRequest.getRealEstateType())
                        , newBankApplication);
                newBankApplication.setCreatedBy(sdId);
                newBankApplication.setUpdatedBy(sdId);
            }
        });
        currentBankApplications.forEach((key, value) -> {
            if (!updateCreditProgramIds.contains(key) && !UNCHANGEABLE_STATUSES.contains(value.getBankApplicationStatus())) {
                value.setActive(false);
            }
        });
        Set<BankApplication> unchangeableStatusBankApplications = partnerApplication.getBankApplications()
                .stream()
                .filter(ba -> UNCHANGEABLE_STATUSES.contains(ba.getBankApplicationStatus()))
                .filter(BaseEntity::isActive)
                .collect(Collectors.toSet());
        List<BankApplication> resultBankApplications = new ArrayList<>(currentBankApplications.values());
        resultBankApplications.addAll(unchangeableStatusBankApplications);
        return resultBankApplications;
    }

    private PartnerApplication getPartnerApplication(PartnerApplicationRequest request, Integer sdId) {
        validateBankApplications(request.getBankApplications());
        RealEstate realEstate = realEstateService.findByRealEstateId(request.getRealEstateId());
        Partner partner = realEstate.getPartner();
        PartnerApplication partnerApplication = partnerApplicationMapper.toPartnerApplication(request)
                .setPartner(partner)
                .setRealEstate(realEstate);
        if (Objects.nonNull(request.getRealEstateTypes())) {
            partnerApplication.setRealEstateTypes(Converter.convertEnumListToString(request.getRealEstateTypes()));
        } else {
            List<RealEstateType> realEstateTypes = request.getBankApplications().stream()
                    .map(BankApplicationRequest::getRealEstateType)
                    .distinct()
                    .collect(Collectors.toList());
            partnerApplication.setRealEstateTypes(Converter.convertEnumListToString(realEstateTypes));
        }
        if (Objects.nonNull(request.getMortgageCalculation())) {
            MortgageCalculation mortgageCalculation = mortgageCalculationMapper.toMortgageCalculation(request.getMortgageCalculation());
            mortgageCalculation.setCreatedBy(sdId);
            mortgageCalculation.setUpdatedBy(sdId);
            partnerApplication.setMortgageCalculation(mortgageCalculation);
        }
        if (Objects.nonNull(request.getPaymentSource())) {
            partnerApplication.setPaymentSource(Converter.convertEnumListToString(request.getPaymentSource()));
        }
        if (Objects.nonNull(request.getInsurances())) {
            partnerApplication.setInsurances(Converter.convertEnumListToString(request.getInsurances()));
        }
        setSalaryBank(request, partnerApplication);
        partnerApplication.setCreatedBy(sdId);
        partnerApplication.setUpdatedBy(sdId);
        return partnerApplication;
    }

    private PartnerApplication getPartnerApplicationById(UUID partnerApplicationId) {
        return partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, String.valueOf(partnerApplicationId)));
    }

    private void validateBankApplications(List<BankApplicationRequest> bankApplications) {
        if (bankApplications == null || bankApplications.isEmpty()) {
            throw new BadRequestException("At least one BankApplicationRequest is required");
        }
        StringBuilder missingFieldsMessage = new StringBuilder();
        for (int i = 0; i < bankApplications.size(); i++) {
            BankApplicationRequest request = bankApplications.get(i);
            List<String> missingFields = new ArrayList<>();

            if (request.getCreditProgramId() == null) {
                missingFields.add("creditProgramId");
            }
            if (request.getDownPayment() == null) {
                missingFields.add("downPayment");
            }
            if (request.getRealEstatePrice() == null) {
                missingFields.add("realEstatePrice");
            }
            if (request.getMonthlyPayment() == null) {
                missingFields.add("monthlyPayment");
            }
            if (request.getCreditTerm() == null) {
                missingFields.add("creditTerm");
            }
            if (request.getOverpayment() == null) {
                missingFields.add("overpayment");
            }
            if (request.getRealEstateType() == null) {
                missingFields.add("realEstateType");
            }
            if (!missingFields.isEmpty()) {
                if (missingFieldsMessage.length() > 0) {
                    missingFieldsMessage.append("; ");
                }
                missingFieldsMessage.append("BankApplicationRequest ");
                if (request.getCreditProgramId() != null) {
                    missingFieldsMessage.append("with creditProgramId '")
                            .append(request.getCreditProgramId())
                            .append("' ");
                } else {
                    missingFieldsMessage.append("at index ")
                            .append(i)
                            .append(" ");
                }
                missingFieldsMessage.append("is missing fields: ")
                        .append(String.join(", ", missingFields));
            }
        }
        if (missingFieldsMessage.length() > 0) {
            throw new BadRequestException(missingFieldsMessage.toString().trim());
        }
    }
}
