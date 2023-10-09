package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.BorrowerRealEstateDto;
import pro.mbroker.api.dto.BorrowerVehicleDto;
import pro.mbroker.api.dto.EmployerDto;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.BorrowerProfileUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.EmploymentStatus;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerEmployer;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.BorrowerRealEstate;
import pro.mbroker.app.entity.BorrowerVehicle;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ItemConflictException;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.exception.ProfileUpdateException;
import pro.mbroker.app.mapper.BorrowerEmployerMapper;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.mapper.BorrowerRealEstateMapper;
import pro.mbroker.app.mapper.BorrowerVehicleMapper;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerProfileServiceImpl implements BorrowerProfileService {
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final BankApplicationService bankApplicationService;
    private final BankService bankService;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final BorrowerEmployerMapper borrowerEmployerMapper;
    private final BorrowerRealEstateMapper borrowerRealEstateMapper;
    private final BorrowerVehicleMapper borrowerVehicleMapper;
    private final PartnerApplicationService partnerApplicationService;

    @Override
    @Transactional
    public BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(request.getId());
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        List<BorrowerProfile> borrowerProfilesToSave = new ArrayList<>();
        if (Objects.nonNull(request.getCoBorrower())) {
            List<BorrowerProfile> coBorrowerProfiles = request.getCoBorrower().stream()
                    .map(borrower -> prepareBorrowerProfile(partnerApplication, borrower))
                    .collect(Collectors.toList());
            borrowerProfilesToSave.addAll(coBorrowerProfiles);
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            BorrowerProfile mainBorrowerProfile = prepareBorrowerProfile(partnerApplication, request.getMainBorrower());
            borrowerProfilesToSave.add(mainBorrowerProfile);
        }
        borrowerProfileRepository.saveAll(borrowerProfilesToSave);
        borrowerProfileRepository.flush();
        return getBorrowersByBankApplicationId(bankApplication.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(partnerApplicationId);
        BorrowerProfile mainBorrower;
        if (partnerApplication.getBankApplications().isEmpty()) {
            throw new ItemNotFoundException(BankApplication.class, partnerApplication.getId() +
                    "Could not find any bank applications with an associated client profile");
        } else {
            mainBorrower = partnerApplication.getBankApplications().get(0).getMainBorrower();
        }
        Map<UUID, BorrowerProfileResponse> coBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .filter(borrowerProfile -> !borrowerProfile.getId().equals(mainBorrower.getId()) && borrowerProfile.isActive())
                .collect(Collectors.toMap(BorrowerProfile::getId, borrowerProfileMapper::toBorrowerProfileResponse));

        List<BorrowerProfileResponse> sortedCoBorrowerProfiles = new ArrayList<>(coBorrowerProfiles.values());
        sortedCoBorrowerProfiles.sort(Comparator.comparing(BorrowerProfileResponse::getCreatedAt));

        return new BorrowerResponse()
                .setMainBorrower(borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower))
                .setCoBorrower(sortedCoBorrowerProfiles);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowersByBankApplicationId(UUID bankApplicationId) {
        BankApplication bankApplication = bankApplicationService.getBankApplicationById(bankApplicationId);
        PartnerApplication partnerApplication = bankApplication.getPartnerApplication();
        BorrowerProfile mainBorrower = bankApplication.getMainBorrower();
        Map<UUID, BorrowerProfileResponse> coBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .filter(borrowerProfile -> !borrowerProfile.getId().equals(mainBorrower.getId()) && borrowerProfile.isActive())
                .collect(Collectors.toMap(BorrowerProfile::getId, borrowerProfileMapper::toBorrowerProfileResponse));
        return new BorrowerResponse()
                .setMainBorrower(borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower))
                .setCoBorrower(new ArrayList<>(coBorrowerProfiles.values()));
    }

    @Override
    @Transactional
    public void updateBorrowerProfileField(UUID borrowerProfileId, BorrowerProfileUpdateRequest updateRequest) {
        BorrowerProfile borrowerProfile = findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        for (Field field : BorrowerProfileUpdateRequest.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(updateRequest);
                if (value != null) {
                    Field borrowerProfileField = BorrowerProfile.class.getDeclaredField(field.getName());
                    borrowerProfileField.setAccessible(true);
                    if (field.getName().equals("employer") && value instanceof EmployerDto) {
                        BorrowerEmployer employer = borrowerProfile.getEmployer() != null
                                ? borrowerProfile.getEmployer()
                                : new BorrowerEmployer();
                        BorrowerEmployer convertedEmployer = convertToBorrowerEmployer((EmployerDto) value, employer);
                        Objects.requireNonNull(convertedEmployer).setBorrowerProfile(borrowerProfile);
                        borrowerProfile.setEmployer(convertedEmployer);
                    } else if (field.getName().equals("realEstate") && value instanceof BorrowerRealEstateDto) {
                        BorrowerRealEstate realEstate = borrowerProfile.getRealEstate();
                        BorrowerRealEstate convertedToBorrowerRealEstate = convertToBorrowerRealEstate((BorrowerRealEstateDto) value, realEstate);
                        Objects.requireNonNull(convertedToBorrowerRealEstate).setBorrowerProfile(borrowerProfile);
                        borrowerProfile.setRealEstate(convertedToBorrowerRealEstate);
                    } else if (field.getName().equals("vehicle") && value instanceof BorrowerVehicleDto) {
                        BorrowerVehicle vehicle = borrowerProfile.getVehicle();
                        BorrowerVehicle convertToBorrowerVehicle = convertToBorrowerVehicle((BorrowerVehicleDto) value, vehicle);
                        Objects.requireNonNull(convertToBorrowerVehicle).setBorrowerProfile(borrowerProfile);
                        borrowerProfile.setVehicle(convertToBorrowerVehicle);
                    } else {
                        borrowerProfileField.set(borrowerProfile, value);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ProfileUpdateException(field.getName(), "Ошибка при обновлении профиля заемщика");
            }
        }
        checkAndUpdateStatus(borrowerProfile);
        partnerApplicationService.statusChanger(borrowerProfile.getPartnerApplication());
        borrowerProfileRepository.save(borrowerProfile);
    }

    private void checkAndUpdateStatus(BorrowerProfile profile) {
        if (isBorrowerMainInfoComplete(profile)
                && isPassportInfoComplete(profile)
                && isEmployerInfoComplete(profile)
                && isIncomeInfoComplete(profile)) {
            bankApplicationService.getBankApplicationByBorrowerId(profile.getId())
                    .stream()
                    .findFirst()
                    .ifPresent(bankApplication -> {
                        if (profile.getSignedForm() != null) {
                            profile.setBorrowerProfileStatus(BorrowerProfileStatus.DOCS_SIGNED);
                            bankApplicationService.changeStatus(bankApplication.getId(), BankApplicationStatus.READY_TO_SENDING);
                        } else {
                            profile.setBorrowerProfileStatus(BorrowerProfileStatus.DATA_ENTERED);
                        }
                    });

        }
    }


    private boolean isEmployerInfoComplete(BorrowerProfile profile) {
        BorrowerEmployer employer = profile.getEmployer();
        if (profile.getEmploymentStatus() != null && profile.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED)
            return true;
        else {
            return profile.getEmploymentStatus() != null
                    && profile.getTotalWorkExperience() != null
                    && !StringUtils.isEmpty(employer.getName())
                    && employer.getBranch() != null
                    && employer.getInn() != null
                    && employer.getInn() > 9
                    && !StringUtils.isEmpty(employer.getPhone())
                    && employer.getNumberOfEmployees() != null
                    && employer.getOrganizationAge() != null
                    && !StringUtils.isEmpty(employer.getAddress())
                    && employer.getWorkExperience() != null
                    && !StringUtils.isEmpty(employer.getPosition());
        }
    }

    private boolean isIncomeInfoComplete(BorrowerProfile profile) {
        return profile.getMainIncome() != null && profile.getProofOfIncome() != null;
    }

    private boolean isDocumentUploaded(List<BorrowerDocument> documents) {
        List<DocumentType> documentTypes = Arrays.asList(DocumentType.values());//todo возможно тут надо жестко прописат какие документы должны быть
        // иначе может появится не обязательный документ и метод упадет
        return documents.stream().allMatch(document -> documentTypes.contains(document.getDocumentType()));

    }

    private boolean isPassportInfoComplete(BorrowerProfile profile) {
        return !StringUtils.isEmpty(profile.getPassportNumber())
                && profile.getPassportIssuedDate() != null
                && !StringUtils.isEmpty(profile.getPassportIssuedByName())
                && !StringUtils.isEmpty(profile.getRegistrationAddress());
        //todo узнать как проверит галочку совпадает регистрация с факт жильем или нет и добавить
        //profile.getResidenceAddress();

    }

    private boolean isBorrowerMainInfoComplete(BorrowerProfile profile) {

        return profile.getEmployer() != null
                && !StringUtils.isEmpty(profile.getFirstName())
                && !StringUtils.isEmpty(profile.getLastName())
                && profile.getPhoneNumber() != null
                && profile.getPhoneNumber().length() == 10
                && profile.getBirthdate() != null
                && profile.getGender() != null
                && !StringUtils.isEmpty(profile.getSnils());
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerProfile getBorrowerProfile(UUID borrowerProfileId) {
        return borrowerProfileRepository.findById(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerProfile findByIdWithRealEstateVehicleAndEmployer(UUID borrowerProfileId) {
        return borrowerProfileRepository.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerProfile.class, borrowerProfileId));
    }

    @Override
    @Transactional
    public void deleteSignatureForm(Long id) {
        borrowerProfileRepository.findBorrowerProfileBySignedFormId(id)
                .ifPresent(borrower -> {
                    borrower.setSignedForm(null);
                    borrowerProfileRepository.save(borrower);
                });
    }

    @Override
    @Transactional
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplication(request.getId());
        List<BorrowerProfile> borrowerProfilesToSave = new ArrayList<>();
        if (Objects.nonNull(request.getCoBorrower())) {
            List<BorrowerProfile> coBorrowerProfiles = request.getCoBorrower().stream()
                    .map(borrower -> prepareBorrowerProfile(partnerApplication, borrower))
                    .collect(Collectors.toList());
            borrowerProfilesToSave.addAll(coBorrowerProfiles);
        }
        if (Objects.nonNull(request.getMainBorrower())) {
            BorrowerProfile mainBorrowerProfile = prepareBorrowerProfile(partnerApplication, request.getMainBorrower());
            borrowerProfilesToSave.add(mainBorrowerProfile);
        }
        borrowerProfileRepository.saveAll(borrowerProfilesToSave);
        borrowerProfileRepository.flush();
        return getBorrowersByPartnerApplicationId(request.getId());
    }

    @Override
    public void deleteBorrowerProfileById(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = getBorrowerProfile(borrowerProfileId);
        PartnerApplication partnerApplication = borrowerProfile.getPartnerApplication();
        List<UUID> mainBorrowerIds = partnerApplication.getBankApplications().stream()
                .map(bankApplication ->
                        bankApplication.getMainBorrower().getId())
                .collect(Collectors.toList());
        if (!mainBorrowerIds.contains(borrowerProfile.getId())) {
            borrowerProfile.setActive(false);
            borrowerProfileRepository.save(borrowerProfile);
        } else {
            throw new ItemConflictException(BorrowerProfile.class, borrowerProfileId);
        }
    }

    private BorrowerProfile prepareBorrowerProfile(PartnerApplication partnerApplication, BorrowerProfileRequest borrower) {
        BorrowerProfile borrowerProfile;
        if (borrower.getId() != null) {
            borrowerProfile = borrowerProfileRepository.findById(borrower.getId())
                    .map(existingBorrowerProfile -> {
                        borrowerProfileMapper.updateBorrowerProfile(borrower, existingBorrowerProfile);
                        return existingBorrowerProfile;
                    })
                    .orElseGet(() -> {
                        BorrowerProfile newBorrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrower);
                        newBorrowerProfile.setPartnerApplication(partnerApplication);
                        return newBorrowerProfile;
                    });
        } else {
            borrowerProfile = borrowerProfileMapper.toBorrowerProfile(borrower);
            borrowerProfile.setPartnerApplication(partnerApplication);
        }

        return borrowerProfile;
    }

    private BorrowerVehicle convertToBorrowerVehicle(BorrowerVehicleDto dto, BorrowerVehicle vehicle) {
        if (Objects.isNull(dto)) {
            return null;
        }
        BorrowerVehicle entity = (Objects.nonNull(vehicle)) ? vehicle : new BorrowerVehicle();
        borrowerVehicleMapper.updateBorrowerVehicleFromDto(dto, entity);
        return entity;
    }

    private BorrowerRealEstate convertToBorrowerRealEstate(BorrowerRealEstateDto dto, BorrowerRealEstate realEstate) {
        if (Objects.isNull(dto)) {
            return null;
        }
        BorrowerRealEstate entity = (Objects.nonNull(realEstate)) ? realEstate : new BorrowerRealEstate();
        borrowerRealEstateMapper.updateBorrowerRealEstateFromDto(dto, entity);
        return entity;
    }

    private BorrowerEmployer convertToBorrowerEmployer(EmployerDto dto, BorrowerEmployer employer) {
        if (Objects.isNull(dto)) {
            return null;
        }
        BorrowerEmployer entity = Optional.ofNullable(employer).orElse(new BorrowerEmployer());
        borrowerEmployerMapper.updateBorrowerEmployerFromDto(dto, entity);
        if (dto.getSalaryBanks() == null) {
            entity.getSalaryBanks().clear();
            return entity;
        }
        Set<UUID> dtoBankIds = new HashSet<>(dto.getSalaryBanks());
        Set<Bank> updatedBanks = entity.getSalaryBanks().stream()
                .filter(bank -> dtoBankIds.contains(bank.getId()))
                .collect(Collectors.toSet());
        dtoBankIds.removeAll(entity.getSalaryBanks().stream().map(Bank::getId).collect(Collectors.toSet()));
        List<Bank> banksToAdd = bankService.getAllBankByIds(dtoBankIds);
        updatedBanks.addAll(banksToAdd);
        entity.setSalaryBanks(updatedBanks);
        return entity;
    }

    @Override
    @Transactional
    public void updateBorrowerStatus(UUID borrowerProfileId, BorrowerProfileStatus status) {
        borrowerProfileRepository.updateBorrowerProfileStatus(borrowerProfileId, status);
    }
}
