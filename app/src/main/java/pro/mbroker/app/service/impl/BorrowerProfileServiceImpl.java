package pro.mbroker.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.ContentCachingRequestWrapper;
import pro.mbroker.api.dto.request.BorrowerEmployerRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.BorrowerProfileUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.api.enums.Education;
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
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.BankApplicationService;
import pro.mbroker.app.service.BankService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.LinkService;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.StatusService;
import pro.mbroker.app.util.Converter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowerProfileServiceImpl implements BorrowerProfileService {
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final BankApplicationService bankApplicationService;
    private final StatusService statusService;
    private final LinkService linkService;
    private final BankService bankService;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final PartnerApplicationService partnerApplicationService;
    private final ObjectMapper objectMapper;

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
        partnerApplication.setBorrowerProfiles(borrowerProfilesToSave);
        statusService.statusChanger(partnerApplication);
        partnerApplicationService.save(partnerApplication);
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
            List<BorrowerDocument> activeBorrowerDocuments = getActiveBorrowerDocuments(mainBorrower.getBorrowerDocument());
            mainBorrower.setBorrowerDocument(activeBorrowerDocuments);
        }

        Map<UUID, BorrowerProfileResponse> coBorrowerProfiles = partnerApplication.getBorrowerProfiles()
                .stream()
                .filter(borrowerProfile ->
                        !borrowerProfile.getId().equals(mainBorrower.getId()) && borrowerProfile.isActive()
                ).map(co -> co.setBorrowerDocument(getActiveBorrowerDocuments(co.getBorrowerDocument())))
                .collect(Collectors.toMap(BorrowerProfile::getId, borrowerProfileMapper::toBorrowerProfileResponse));

        List<BorrowerProfileResponse> sortedCoBorrowerProfiles = new ArrayList<>(coBorrowerProfiles.values());
        sortedCoBorrowerProfiles.sort(Comparator.comparing(BorrowerProfileResponse::getCreatedAt));
        return new BorrowerResponse()
                .setMainBorrower(borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower))
                .setCoBorrower(sortedCoBorrowerProfiles);
    }

    private List<BorrowerDocument> getActiveBorrowerDocuments(List<BorrowerDocument> documents) {
        if (documents == null) {
            return Collections.emptyList();
        }

        List<BorrowerDocument> attacments = new ArrayList<>(documents.stream()
                .filter(d -> d.isActive() && d.getDocumentType() != DocumentType.APPLICATION_FORM
                )
                .collect(Collectors.toList()));

        Optional<BorrowerDocument> latestApplicationForm = documents.stream()
                .filter(document -> document.getDocumentType() == DocumentType.APPLICATION_FORM)
                .max(Comparator.comparing(BorrowerDocument::getCreatedAt));

        latestApplicationForm.ifPresent(attacments::add);

        return attacments;
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
        List<BorrowerProfileResponse> sortedCoBorrowerProfiles = new ArrayList<>(coBorrowerProfiles.values());
        sortedCoBorrowerProfiles.sort(Comparator.comparing(BorrowerProfileResponse::getCreatedAt));
        return new BorrowerResponse()
                .setMainBorrower(borrowerProfileMapper.toBorrowerProfileResponse(mainBorrower))
                .setCoBorrower(sortedCoBorrowerProfiles);
    }

    @Override
    public void updateBorrowerProfileField(UUID borrowerProfileId, BorrowerProfileUpdateRequest updateRequest, HttpServletRequest request) {
        Map<String, Object> fieldsMap = extractFieldsFromRequest(request);
        if (fieldsMap.isEmpty()) return;
        BorrowerProfile borrowerProfile = findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = BorrowerProfileUpdateRequest.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.getType().isEnum()) {
                    updateEnumField(borrowerProfile, field, fieldsMap);
                } else if (List.class.isAssignableFrom(field.getType())) {
                    updateArrayField(borrowerProfile, field, fieldsMap);
                } else {
                    Field borrowerProfileField = BorrowerProfile.class.getDeclaredField(field.getName());
                    borrowerProfileField.setAccessible(true);
                    if ("employer".equals(fieldName) && value instanceof Map) {
                        updateEmployerField(borrowerProfile, fieldsMap, value);
                    } else if ("realEstate".equals(fieldName) && value instanceof Map) {
                        updateRealEstateField(borrowerProfile, fieldsMap);
                    } else if ("vehicle".equals(fieldName) && value instanceof Map) {
                        updateVehicleField(borrowerProfile, fieldsMap);
                    } else {
                        if (borrowerProfileField.getType().equals(LocalDate.class) && value instanceof String) {
                            LocalDate dateValue = LocalDate.parse((String) value);
                            borrowerProfileField.set(borrowerProfile, dateValue);
                        } else {
                            borrowerProfileField.set(borrowerProfile, value);
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ProfileUpdateException(fieldName, "Ошибка при обновлении профиля заемщика");
            }
        }
        deleteBorrowerDocuments(borrowerProfile);
        statusService.statusChanger(borrowerProfile.getPartnerApplication());
        partnerApplicationService.save(borrowerProfile.getPartnerApplication());
    }

    private void updateArrayField(BorrowerProfile borrowerProfile, Field field, Map<String, Object> fieldsMap) throws NoSuchFieldException, IllegalAccessException {
        if ("educations".equals(field.getName())) {
            @SuppressWarnings("unchecked")
            List<String> educationStrings = (List<String>) fieldsMap.get(field.getName());
            if (educationStrings != null) {
                List<Education> educationEnums = educationStrings.stream()
                        .map(Education::valueOf)
                        .collect(Collectors.toList());
                String educationsAsString = Converter.convertEnumListToString(educationEnums);
                Field borrowerProfileField = BorrowerProfile.class.getDeclaredField(field.getName());
                borrowerProfileField.setAccessible(true);
                borrowerProfileField.set(borrowerProfile, educationsAsString);
            } else {
                Field borrowerProfileField = BorrowerProfile.class.getDeclaredField(field.getName());
                borrowerProfileField.setAccessible(true);
                borrowerProfileField.set(borrowerProfile, null);
            }
        }
    }

    private void deleteBorrowerDocuments(BorrowerProfile profile) {
        EnumSet<DocumentType> typesToDelete = EnumSet.of(
                DocumentType.APPLICATION_FORM,
                DocumentType.GENERATED_FORM,
                DocumentType.GENERATED_SIGNATURE_FORM);

        List<BorrowerDocument> documents = profile.getBorrowerDocument();
        documents.stream()
                .filter(document -> typesToDelete.contains(document.getDocumentType()))
                .forEach(document -> document.setActive(false));
    }

    private void updateEnumField(BorrowerProfile borrowerProfile, Field field, Map<String, Object> fieldsMap) throws NoSuchFieldException, IllegalAccessException {
        String enumValue = (String) fieldsMap.get(field.getName());
        Field borrowerProfileField = BorrowerProfile.class.getDeclaredField(field.getName());
        borrowerProfileField.setAccessible(true);
        if (enumValue != null) {
            Object updatedEnum = Enum.valueOf((Class<Enum>) field.getType(), enumValue);
            borrowerProfileField.set(borrowerProfile, updatedEnum);
        } else borrowerProfileField.set(borrowerProfile, null);
    }

    private void updateEmployerField(BorrowerProfile borrowerProfile, Map<String, Object> fieldsMap, Object value) {
        BorrowerEmployer employer = borrowerProfile.getEmployer() != null ? borrowerProfile.getEmployer() : new BorrowerEmployer();
        updateObjectWithSalaryBankValues(fieldsMap, value, employer);
        updateObjectWithEnumsAndValues((Map<String, Object>) fieldsMap.get("employer"), employer, BorrowerEmployer.class);
        Objects.requireNonNull(employer).setBorrowerProfile(borrowerProfile);
        borrowerProfile.setEmployer(employer);
    }

    private void updateObjectWithSalaryBankValues(Map<String, Object> fieldsMap, Object value, BorrowerEmployer employer) {
        BorrowerEmployerRequest borrowerEmployerRequest = new ObjectMapper().convertValue(value, BorrowerEmployerRequest.class);
        Object employerObject = fieldsMap.get("employer");
        if (employerObject instanceof Map<?, ?>) {
            Map<?, ?> employerMap = (Map<?, ?>) employerObject;
            if (employerMap.containsKey("salaryBanks")) {
                updateSalaryBank(borrowerEmployerRequest, employer);
            }
        }
    }

    private void updateRealEstateField(BorrowerProfile borrowerProfile, Map<String, Object> fieldsMap) {
        BorrowerRealEstate realEstate = borrowerProfile.getRealEstate() != null ? borrowerProfile.getRealEstate() : new BorrowerRealEstate();
        Map<String, Object> realEstateFieldsMap = (Map<String, Object>) fieldsMap.get("realEstate");
        updateObjectWithEnumsAndValues(realEstateFieldsMap, realEstate, BorrowerRealEstate.class);
        Objects.requireNonNull(realEstate).setBorrowerProfile(borrowerProfile);
        borrowerProfile.setRealEstate(realEstate);
    }

    private void updateVehicleField(BorrowerProfile borrowerProfile, Map<String, Object> fieldsMap) {
        BorrowerVehicle vehicle = borrowerProfile.getVehicle() != null ? borrowerProfile.getVehicle() : new BorrowerVehicle();
        Map<String, Object> vehicleFieldsMap = (Map<String, Object>) fieldsMap.get("vehicle");
        updateObjectWithEnumsAndValues(vehicleFieldsMap, vehicle, BorrowerVehicle.class);
        Objects.requireNonNull(vehicle).setBorrowerProfile(borrowerProfile);
        borrowerProfile.setVehicle(vehicle);

    }

    private <T> void updateObjectWithEnumsAndValues(Map<String, Object> fieldsMap, T targetObject, Class<T> targetClass) {
        for (Field field : targetClass.getDeclaredFields()) {
            if (fieldsMap.containsKey(field.getName()) && !field.getName().equals("salaryBanks")) {
                field.setAccessible(true);
                if (field.getType().isEnum()) setField(targetObject, field, getEnumValue(field, fieldsMap));
                else setField(targetObject, field, fieldsMap.get(field.getName()));
            }
        }
    }

    private Object getEnumValue(Field field, Map<String, Object> fieldsMap) {
        String enumValue = (String) fieldsMap.get(field.getName());
        if (enumValue != null) return Enum.valueOf((Class<Enum>) field.getType(), enumValue);
        return null;
    }

    private <T> void setField(T targetObject, Field field, Object value) {
        try {
            if (field.getType().equals(Float.class) && value instanceof Integer) {
                Float floatValue = ((Integer) value).floatValue();
                field.set(targetObject, floatValue);
            } else {
                field.set(targetObject, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> extractFieldsFromRequest(HttpServletRequest request) {
        new ContentCachingRequestWrapper(request);
        ContentCachingRequestWrapper wrappedRequest;
        String jsonPayload;
        if (request instanceof ContentCachingRequestWrapper) {
            wrappedRequest = (ContentCachingRequestWrapper) request;
            byte[] content = wrappedRequest.getContentAsByteArray();
            jsonPayload = new String(content, StandardCharsets.UTF_8);
        } else {
            log.warn("Request is not an instance of ContentCachingRequestWrapper");
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(jsonPayload, Map.class);
        } catch (Exception e) {
            log.error("Error during reading request payload", e);
            return Collections.emptyMap();
        }
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
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request, HttpServletRequest httpRequest) {
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
        linkService.addLinksByProfiles(borrowerProfilesToSave, httpRequest);
        partnerApplicationService.save(partnerApplication);
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
            statusService.statusChanger(partnerApplication);
            partnerApplicationService.save(partnerApplication);
        } else {
            throw new ItemConflictException(BorrowerProfile.class, borrowerProfileId);
        }
    }

    @Override
    @Transactional
    public void updateBorrowerStatus(UUID borrowerProfileId, BorrowerProfileStatus status) {
        borrowerProfileRepository.updateBorrowerProfileStatus(borrowerProfileId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowerProfile getFullBorrower(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        partnerApplicationService.checkPermission(borrowerProfile.getPartnerApplication());
        return borrowerProfile;
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

    private void updateSalaryBank(BorrowerEmployerRequest dto, BorrowerEmployer employer) {
        if (Objects.isNull(dto)) {
            return;
        }
        if (dto.getSalaryBanks() == null) {
            employer.getSalaryBanks().clear();
            return;
        }
        Set<UUID> dtoBankIds = new HashSet<>(dto.getSalaryBanks());
        Set<Bank> updatedBanks = employer.getSalaryBanks().stream()
                .filter(bank -> dtoBankIds.contains(bank.getId()))
                .collect(Collectors.toSet());
        dtoBankIds.removeAll(employer.getSalaryBanks().stream().map(Bank::getId).collect(Collectors.toSet()));
        List<Bank> banksToAdd = bankService.getAllBankByIds(dtoBankIds);
        updatedBanks.addAll(banksToAdd);
        employer.setSalaryBanks(updatedBanks);
    }

}
