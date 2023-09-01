package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.BankWithBankApplicationDto;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.api.dto.SalaryClientProgramCalculationDto;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.BankApplicationResponse;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.*;
import pro.mbroker.app.entity.*;
import pro.mbroker.app.exception.AccessDeniedException;
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
import pro.mbroker.app.service.*;
import pro.mbroker.app.util.Converter;
import pro.mbroker.app.util.Pagination;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.common.security.Permission;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationServiceImpl implements PartnerApplicationService {

    private static final String CREATED_AT = "createdAt";

    private final CalculatorService calculatorService;
    private final CurrentUserService currentUserService;
    private final AttachmentService attachmentService;
    private final PartnerService partnerService;
    private final RealEstateService realEstateService;
    private final CreditProgramService creditProgramService;
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final BankRepository bankRepository;
    private final PartnerApplicationMapper partnerApplicationMapper;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final BankApplicationMapper bankApplicationMapper;
    private final MortgageCalculationMapper mortgageCalculationMapper;

    private static final List<DocumentType> REQUIRED_DOCUMENT_TYPES =
            Arrays.asList(DocumentType.BORROWER_PASSPORT, DocumentType.BORROWER_SNILS);

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
    public Page<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder, LocalDateTime startDate, LocalDateTime endDate) {
        Optional<LocalDateTime> start = Optional.ofNullable(startDate);
        Optional<LocalDateTime> end = Optional.ofNullable(endDate);
        log.info("Getting all partner applications with pagination: page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);
        Pageable pageable = Pagination.createPageable(page, size, sortBy, sortOrder);
        Page<PartnerApplication> result = Page.empty(pageable);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority(Permission.Code.MB_ADMIN_ACCESS.toString()))) {
                result = partnerApplicationRepository.findAllByIsActiveTrue(start, end, pageable);
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_ORGANIZATION.toString()))) {
                UUID partnerId = partnerService.getCurrentPartner().getId();
                result = partnerApplicationRepository.findAllIsActiveByPartnerId(start, end, partnerId, pageable);
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_OWN.toString()))) {
                Integer createdBy = TokenExtractor.extractSdId(currentUserService.getCurrentUserToken());
                result = partnerApplicationRepository.findAllByCreatedByAndIsActiveTrue(start, end, createdBy, pageable);
            } else {
                log.warn("User does not have any valid permission to fetch partner applications");
            }
            result.getContent().forEach(partnerApplication -> {
                List<BorrowerProfile> sortedBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                        .stream()
                        .sorted(Comparator.comparing(BorrowerProfile::getCreatedAt))
                        .collect(Collectors.toList());
                partnerApplication.setBorrowerProfiles(sortedBorrowerProfiles);
            });

        } catch (Exception e) {
            log.error("Error while fetching partner applications", e);
        }

        return result;
    }

    @Override
    @Transactional
    public PartnerApplication changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        BorrowerProfile borrowerProfile = borrowerProfileRepository.findById(newMainBorrowerId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, newMainBorrowerId));
        partnerApplication.getBankApplications()
                .forEach(bankApplication -> {
                    if (!UNCHANGEABLE_STATUSES.contains(bankApplication.getBankApplicationStatus()))
                        bankApplication.setMainBorrower(borrowerProfile);
                });
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    @Transactional
    public PartnerApplication createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = getPartnerApplication(request);
        List<BankApplication> bankApplications = buildBankApplications(request.getBankApplications(), partnerApplication);
        partnerApplication.setBankApplications(bankApplications);
        updateMainBorrower(partnerApplication, request.getMainBorrower());
        return statusChanger(partnerApplication);
    }

    @Override
    @Transactional
    public PartnerApplication updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication existingPartnerApplication = getPartnerApplicationByIdWithPermission(partnerApplicationId);
        if (isBorrowerProfileChanged(request, existingPartnerApplication)) {
            deleteBorrowerDocuments(existingPartnerApplication);
        }
        partnerApplicationMapper.updatePartnerApplicationFromRequest(request, existingPartnerApplication);
        mortgageCalculationMapper.updateMortgageCalculationFromRequest(request.getMortgageCalculation(), existingPartnerApplication.getMortgageCalculation());
        setSalaryBank(request, existingPartnerApplication);
        existingPartnerApplication.setRealEstate(realEstateService.findById(request.getRealEstateId()));
        List<BankApplication> updatedBorrowerApplications = buildBankApplications(request.getBankApplications(), existingPartnerApplication);
        existingPartnerApplication.setBankApplications(updatedBorrowerApplications);
        updateMainBorrower(existingPartnerApplication, request.getMainBorrower());
        return statusChanger(existingPartnerApplication);
    }

    private void setSalaryBank(PartnerApplicationRequest request, PartnerApplication existingPartnerApplication) {
        if (Objects.nonNull(request.getMortgageCalculation().getSalaryBanks())) {
            List<Bank> banks = bankRepository.findAllById(request.getMortgageCalculation().getSalaryBanks());
            existingPartnerApplication.getMortgageCalculation().setSalaryBanks(banks);
        }
    }

    private boolean isBorrowerProfileChanged(PartnerApplicationRequest request, PartnerApplication existingApplication) {
        if (!Objects.equals(request.getCreditPurposeType(), existingApplication.getCreditPurposeType())) {
            return true;
        }
        if (!Objects.equals(request.getRealEstateType(), existingApplication.getRealEstateType())) {
            return true;
        }
        UUID existingRealEstateId = existingApplication.getRealEstate() != null ? existingApplication.getRealEstate().getId() : null;
        if (!Objects.equals(request.getRealEstateId(), existingRealEstateId)) {
            return true;
        }
        MortgageCalculationDto requestMortgageCalculation = request.getMortgageCalculation();
        MortgageCalculation existingMortgageCalculation = existingApplication.getMortgageCalculation();
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


    private void deleteBorrowerDocuments(PartnerApplication application) {
        application.getBorrowerProfiles().forEach(borrowerProfile -> {
            List<BorrowerDocument> documents = borrowerProfile.getBorrowerDocument();
            documents.stream()
                    .filter(document -> document.getDocumentType().equals(DocumentType.APPLICATION_FORM))
                    .forEach(document -> document.setActive(false));
        });
    }


    @Override
    @Transactional
    public PartnerApplication statusChanger(PartnerApplication partnerApplication) {
        if (Objects.isNull(partnerApplication.getPartnerApplicationStatus())) {
            partnerApplication.setPartnerApplicationStatus(PartnerApplicationStatus.UPLOADING_DOCS);
        }
        partnerApplication.getBankApplications().forEach(bankApplication -> {
            if (Objects.isNull(bankApplication.getBankApplicationStatus())) {
                bankApplication.setBankApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED);
            }
        });
        partnerApplication.getBorrowerProfiles().forEach(borrowerProfile -> {
            if (Objects.isNull(borrowerProfile.getBorrowerProfileStatus())) {
                borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_NO_ENTERED);
            }
        });
        if (Objects.nonNull(partnerApplication.getId())) {
            checkDocumentStatus(partnerApplication);
        }
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Transactional
    public void checkDocumentStatus(PartnerApplication partnerApplication) {
        List<RequiredDocumentResponse> requiredDocuments = getRequiredDocuments(partnerApplication.getId())
                .stream()
                .filter(requiredDocument ->
                        requiredDocument.getDocumentType() != DocumentType.DATA_PROCESSING_AGREEMENT &&
                                requiredDocument.getDocumentType() != DocumentType.CERTIFIED_COPY_TK &&
                                requiredDocument.getDocumentType() != DocumentType.INCOME_CERTIFICATE)
                .collect(Collectors.toList());
        for (BorrowerProfile borrowerProfile : partnerApplication.getBorrowerProfiles()) {
            List<BorrowerDocument> borrowerDocuments = borrowerProfile.getBorrowerDocument().stream()
                    .filter(BorrowerDocument::isActive)
                    .collect(Collectors.toList());
            boolean allDocumentsPresent = requiredDocuments.stream().allMatch(requiredDocument -> {
                DocumentType requiredType = requiredDocument.getDocumentType();
                UUID requiredBankId = requiredDocument.getBankId();
                return borrowerDocuments.stream().anyMatch(borrowerDocument ->
                        borrowerDocument.getDocumentType() == requiredType &&
                                (requiredType != DocumentType.APPLICATION_FORM ||
                                        borrowerDocument.getBank().getId().equals(requiredBankId)));
            });
            if (allDocumentsPresent) {
                borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_ENTERED);
            } else {
                borrowerProfile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_NO_ENTERED);
            }
        }
        checkBankApplicationStatus(partnerApplication);
    }


    @Override
    @Transactional
    public void deletePartnerApplication(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = getPartnerApplicationById(partnerApplicationId);
        partnerApplication.setActive(false);
        partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerApplicationResponse buildPartnerApplicationResponse(PartnerApplication partnerApplication) {
        PartnerApplicationResponse response = partnerApplicationMapper.toPartnerApplicationResponse(partnerApplication);
        Map<UUID, BorrowerProfile> borrowerProfileMap = getActiveBorrowerProfilesMap(partnerApplication);
        List<BankApplicationResponse> activeBankApplicationResponses = getActiveBankApplicationResponses(partnerApplication, borrowerProfileMap);
        List<BankWithBankApplicationDto> bankWithBankApplicationDtos = getGroupBankApplication(activeBankApplicationResponses);
        response.setBankWithBankApplicationDto(bankWithBankApplicationDtos);
        response.setBorrowerProfiles(new ArrayList<>(borrowerProfileMap.values()).stream()
                .map(borrowerProfileMapper::toBorrowerProfileResponse).collect(Collectors.toList()));
        return response;
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
        if (Objects.nonNull(partnerApplication.getMortgageCalculation().getSalaryBanks()) &&
                partnerApplication.getMortgageCalculation().getSalaryBanks()
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


    private List<BankWithBankApplicationDto> getGroupBankApplication(List<BankApplicationResponse> activeBankApplicationResponses) {
        Map<UUID, BankApplicationResponse> creditProgramMap = activeBankApplicationResponses.stream()
                .collect(Collectors.toMap(BankApplicationResponse::getCreditProgramId, Function.identity()));
        List<CreditProgram> programByCreditProgramIds = creditProgramService.getProgramByCreditProgramIds(new ArrayList<>(creditProgramMap.keySet()));
        Map<UUID, Bank> bankMap = programByCreditProgramIds.stream()
                .map(CreditProgram::getBank)
                .collect(Collectors.toMap(Bank::getId, Function.identity(), (oldValue, newValue) -> oldValue));
        Map<UUID, List<BankApplicationResponse>> grouped = programByCreditProgramIds.stream()
                .collect(Collectors.groupingBy(creditProgram -> creditProgram.getBank().getId(),
                        Collectors.mapping(creditProgram -> creditProgramMap.get(creditProgram.getId()), Collectors.toList())));
        List<BankWithBankApplicationDto> result = new ArrayList<>();
        for (Map.Entry<UUID, List<BankApplicationResponse>> entry : grouped.entrySet()) {
            BankWithBankApplicationDto dto = new BankWithBankApplicationDto();
            Bank bank = bankMap.get(entry.getKey());
            dto.setBankId(entry.getKey());
            dto.setBankName(bank.getName());
            if (Objects.nonNull(bank.getAttachment()) && Objects.nonNull(bank.getAttachment().getId())) {
                Long logoId = bank.getAttachment().getId();
                try {
                    dto.setLogo(Converter.generateBase64FromFile(attachmentService.download(logoId)));
                } catch (Exception e) {
                    log.error("Error loading logo: " + e.getMessage());
                }
            }
            dto.setBankApplications(entry.getValue());
            result.add(dto);
        }
        sortBankWithBankApplicationDto(result);
        return result;
    }

    private static void sortBankWithBankApplicationDto(List<BankWithBankApplicationDto> result) {
        result.forEach(bankWithBankApplicationDto ->
                bankWithBankApplicationDto.getBankApplications()
                        .sort(Comparator.comparing(BankApplicationResponse::getMonthlyPayment))
        );
        result.sort(Comparator.comparing(bankWithBankApplicationDto ->
                bankWithBankApplicationDto.getBankApplications().stream()
                        .min(Comparator.comparing(BankApplicationResponse::getMonthlyPayment))
                        .orElseThrow(NoSuchElementException::new)
                        .getMonthlyPayment()));
    }


    @Override
    @Transactional(readOnly = true)
    public PartnerApplication getPartnerApplicationByIdWithPermission(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_ADMIN_ACCESS))) {
            checkPermission(authorities, partnerApplication);
        }
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
    public List<PartnerApplicationResponse> search(String firstName,
                                                   String middleName,
                                                   String lastName,
                                                   String phoneNumber,
                                                   String residentialComplexName,
                                                   RegionType region,
                                                   String bankName,
                                                   BankApplicationStatus applicationStatus,
                                                   String sortBy,
                                                   String sortDirection) {

        List<PartnerApplication> bankApplications = partnerApplicationRepository
                .findAll(PartnerApplicationSpecification
                        .combineSearch(firstName,
                                middleName,
                                lastName,
                                phoneNumber,
                                residentialComplexName,
                                region,
                                bankName,
                                applicationStatus)
                );

        sortBankApplicationList(sortBy, sortDirection, bankApplications);

        return bankApplications.stream()
                .map(this::buildPartnerApplicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PartnerApplication enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        BankApplication currentBankApplication = currentBankApplications.get(request.getCreditProgramId());
        if (currentBankApplication != null) {
            bankApplicationMapper.updateBankApplicationFromRequest(currentBankApplication, request);
        } else {
            BankApplication newBankApplication = bankApplicationMapper.toBankApplication(request);
            newBankApplication.setPartnerApplication(partnerApplication);
            newBankApplication.setMainBorrower(getBorrowerProfile(request.getMainBorrowerId()));
            currentBankApplications.put(request.getCreditProgramId(), newBankApplication);
        }
        partnerApplication.setBankApplications(new ArrayList<>(currentBankApplications.values()));
        return partnerApplicationRepository.save(partnerApplication);
    }

    @Override
    @Transactional
    public PartnerApplication disableBankApplication(UUID partnerApplicationId, UUID creditProgramId) {
        PartnerApplication partnerApplication = getPartnerApplication(partnerApplicationId);
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        BankApplication disabledBankApplication = currentBankApplications.get(creditProgramId);
        if (disabledBankApplication == null) {
            throw new ItemConflictException(CreditProgram.class, "This PartnerApplication with" + partnerApplicationId + " does not have such a credit program id: " + creditProgramId);
        }
        disabledBankApplication.setActive(false);
        partnerApplication.setBankApplications(new ArrayList<>(currentBankApplications.values()));
        return partnerApplicationRepository.save(partnerApplication);
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

    private void updateMainBorrower(PartnerApplication partnerApplication, BorrowerProfileRequest borrowerProfileRequest) {
        BorrowerProfile borrowerProfile;
        if (borrowerProfileRequest.getId() != null) {
            borrowerProfile = getBorrowerProfile(borrowerProfileRequest.getId());
            borrowerProfileMapper.updateBorrowerProfile(borrowerProfileRequest, borrowerProfile);
        } else {
            if (!partnerApplication.getBorrowerProfiles().isEmpty()) {
                throw new ItemConflictException(BorrowerProfile.class,
                        "The PartnerApplication id: " + partnerApplication.getId() + " already has a mainBorrower. You need to specify the ID of the existing mainBorrower in the request");
            }
            borrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrowerProfileRequest);
            partnerApplication.getBorrowerProfiles().add(borrowerProfile);
        }
        borrowerProfile.setPartnerApplication(partnerApplication);
        partnerApplication.getBankApplications().forEach(app -> app.setMainBorrower(borrowerProfile));
    }

    private BorrowerProfile getBorrowerProfile(UUID borrowerProfileId) {
        return borrowerProfileRepository.findById(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    private List<BankApplication> buildBankApplications(List<BankApplicationRequest> requests, PartnerApplication partnerApplication) {
        List<UUID> updateCreditProgramIds = requests.stream()
                .map(BankApplicationRequest::getCreditProgramId)
                .collect(Collectors.toList());
        MortgageCalculation mortgageCalculation = partnerApplication.getMortgageCalculation();
        if (mortgageCalculation.getIsMaternalCapital() == null) {
            mortgageCalculation.setIsMaternalCapital(false);
        }
        Map<UUID, BankApplication> currentBankApplications = partnerApplication.getBankApplications()
                .stream()
                .collect(Collectors.toMap(bankApplication -> bankApplication.getCreditProgram().getId(), Function.identity()));
        requests.forEach(bankApplicationRequest -> {
            BankApplication currentBankApplication = currentBankApplications.get(bankApplicationRequest.getCreditProgramId());
            if (currentBankApplication != null) {
                bankApplicationMapper.updateBankApplicationFromRequest(currentBankApplication, bankApplicationRequest);
            } else {
                BankApplication newBankApplication = bankApplicationMapper.toBankApplication(bankApplicationRequest)
                        .setPartnerApplication(partnerApplication);
                currentBankApplications.put(bankApplicationRequest.getCreditProgramId(), newBankApplication);
            }
        });
        currentBankApplications.forEach((key, value) -> {
            if (!updateCreditProgramIds.contains(key)) {
                value.setActive(false);
            }
        });
        return new ArrayList<>(currentBankApplications.values());
    }


    private void sortBankApplicationList(String sortBy, String sortDirection, List<PartnerApplication> bankApplications) {
        Comparator<PartnerApplication> comparator;
        if (sortBy != null) {
            if (sortBy.equals(CREATED_AT)) {
                comparator = Comparator.comparing(pa -> pa.getCreatedAt());
            } else {
                comparator = Comparator.comparing(pa -> pa.getCreatedAt());
            }

            if (sortDirection != null && sortDirection.equals("DESC")) {
                comparator = comparator.reversed();
            }

            bankApplications.sort(comparator);
        }
    }


    private void checkPermission(Collection<? extends GrantedAuthority> authorities, PartnerApplication partnerApplication) {
        String currentUserToken = currentUserService.getCurrentUserToken();
        Integer organizationId = TokenExtractor.extractSdCurrentOrganizationId(currentUserToken);
        Integer sdId = TokenExtractor.extractSdId(currentUserToken);
        if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_ORGANIZATION)) &&
                !partnerApplication.getPartner().getSmartDealOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("organization_id: " + organizationId, PartnerApplication.class);
        }
        if (authorities.contains(new SimpleGrantedAuthority(Permission.Code.MB_REQUEST_READ_OWN)) &&
                !partnerApplication.getCreatedBy().equals(sdId)) {
            throw new AccessDeniedException("sd_id: " + sdId, PartnerApplication.class);
        }
    }

    private PartnerApplication getPartnerApplication(PartnerApplicationRequest request) {
        RealEstate realEstate = realEstateService.findById(request.getRealEstateId());
        Partner partner = realEstate.getPartner();
        PartnerApplication partnerApplication = partnerApplicationMapper.toPartnerApplication(request)
                .setPartner(partner)
                .setRealEstate(realEstate)
                .setMortgageCalculation(mortgageCalculationMapper.toMortgageCalculation(request.getMortgageCalculation()));
        setSalaryBank(request, partnerApplication);
        return partnerApplication;
    }

    private PartnerApplication getPartnerApplicationById(UUID partnerApplicationId) {
        return partnerApplicationRepository.findById(partnerApplicationId)
                .orElseThrow(() -> new ItemNotFoundException(PartnerApplication.class, String.valueOf(partnerApplicationId)));
    }

    private void checkBankApplicationStatus(PartnerApplication partnerApplication) {
        List<BankApplication> bankApplications = new ArrayList<>(partnerApplication.getBankApplications());
        bankApplications.removeIf(app -> !app.isActive() || UNCHANGEABLE_STATUSES.contains(app.getBankApplicationStatus()));
        for (BankApplication bankApplication : bankApplications) {
            if (allProfilesHaveRequiredDocuments(bankApplication, partnerApplication)) {
                bankApplication.setBankApplicationStatus(BankApplicationStatus.READY_TO_SENDING);
            } else {
                bankApplication.setBankApplicationStatus(BankApplicationStatus.DATA_NO_ENTERED);
            }
        }
    }

    private boolean allProfilesHaveRequiredDocuments(BankApplication bankApplication, PartnerApplication partnerApplication) {
        Bank bank = bankApplication.getCreditProgram().getBank();
        List<BorrowerProfile> borrowerProfiles = partnerApplication.getBorrowerProfiles();
        return borrowerProfiles.stream()
                .allMatch(borrowerProfile ->
                        checkRequiredDocuments(borrowerProfile.getBorrowerDocument(), bank)
                );
    }

    private boolean checkRequiredDocuments(List<BorrowerDocument> borrowerDocuments, Bank bank) {
        Set<DocumentType> documentTypes = borrowerDocuments.stream()
                .filter(BorrowerDocument::isActive)
                .map(BorrowerDocument::getDocumentType)
                .collect(Collectors.toSet());
        boolean allRequiredDocumentsPresent = documentTypes.containsAll(REQUIRED_DOCUMENT_TYPES);
        boolean applicationFormDocumentPresent = borrowerDocuments.stream()
                .anyMatch(doc -> doc.getDocumentType().equals(DocumentType.APPLICATION_FORM) && doc.getBank().equals(bank));
        return allRequiredDocumentsPresent && applicationFormDocumentPresent;
    }

}
