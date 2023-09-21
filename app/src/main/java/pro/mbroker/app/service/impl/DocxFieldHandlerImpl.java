package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.TraversalUtil;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Service;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerEmployer;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.DocxFieldHandler;
import pro.mbroker.app.util.Converter;
import pro.mbroker.app.util.DocxFieldExtractor;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocxFieldHandlerImpl implements DocxFieldHandler {

    public Map<String, String> replaceFieldValue(byte[] file, PartnerApplication partnerApplication, BorrowerProfile borrowerProfile) {
        Set<String> fields = extractFieldsFromDocx(file);
        BorrowerProfile mainBorrower;
        BankApplication bankApplication;
        OptionalInt maxMonthCreditTerm;
        if (Objects.nonNull(partnerApplication.getBankApplications())) {
            maxMonthCreditTerm = partnerApplication.getBankApplications().stream()
                    .mapToInt(BankApplication::getMonthCreditTerm)
                    .max();
            bankApplication = partnerApplication.getBankApplications().get(0);
            mainBorrower = partnerApplication.getBankApplications().get(0).getMainBorrower();
        } else {
            mainBorrower = null;
            bankApplication = null;
            maxMonthCreditTerm = null;
        }
        Map<String, Function<Void, String>> fieldMapping = new HashMap<>() {
            {
                put("borrowerPassportNumber", (v) -> borrowerProfile.getPassportNumber());
                put("borrowerType", (v) -> (Objects.nonNull(mainBorrower)) ? mainBorrower.getId().equals(borrowerProfile.getId()) ? "Заемщик" : "Созаемщик" : "-");
                put("borrowerEducation", (v) -> (Objects.nonNull(borrowerProfile.getEducation())) ? borrowerProfile.getEducation().getName() : "-");
                put("borrowerTotalWorkExperience", (v) -> (Objects.nonNull(borrowerProfile.getTotalWorkExperience())) ? borrowerProfile.getTotalWorkExperience().getName() : "-");
                put("borrowerMarriageContract", (v) -> (Objects.nonNull(borrowerProfile.getMarriageContract()) ? borrowerProfile.getMarriageContract().getName() : "-"));
                put("borrowerEmploymentStatus", (v) -> (Objects.nonNull(borrowerProfile.getEmploymentStatus())) ? borrowerProfile.getEmploymentStatus().getName() : "-");
                put("borrowerChildren", (v) -> (Objects.nonNull(borrowerProfile.getChildren())) ? borrowerProfile.getChildren().toString() : "-");
                put("borrowerMaritalStatus", (v) -> (Objects.nonNull(borrowerProfile.getMaritalStatus())) ? borrowerProfile.getMaritalStatus().getName() : "-");
                put("borrowerCompanyName", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getName).orElse("-"));
                put("borrowerCompanyInn", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getInn).map(Object::toString).orElse("-"));
                put("borrowerCompanyBranch", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getBranch).map(Branch::getName).orElse("-"));
                put("borrowerCompanyEmployeeCount", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getNumberOfEmployees).map(NumberOfEmployees::getName).orElse("-"));
                put("creditAmount", (v) -> bankApplication.getRealEstatePrice().subtract(bankApplication.getDownPayment()).toString());
                put("realEstateType", (v) -> Optional.ofNullable(bankApplication.getCreditProgram().getCreditProgramDetail().getRealEstateType())
                        .map(types -> Converter.convertStringListToEnumList(types, RealEstateType.class))
                        .map(list -> list.stream()
                                .map(RealEstateType::getName)
                                .collect(Collectors.joining("\n")))
                        .orElse(""));
                put("creditPurposeType", (v) -> Optional.ofNullable(bankApplication.getCreditProgram().getCreditProgramDetail().getCreditPurposeType())
                        .map(types -> Converter.convertStringListToEnumList(types, CreditPurposeType.class))
                        .map(list -> list.stream()
                                .map(CreditPurposeType::getName)
                                .collect(Collectors.joining("\n")))
                        .orElse(""));
                put("realEstateRegion", (v) -> bankApplication.getPartnerApplication().getRealEstate().getRegion().getName());
                put("borrowerRegistrationAddress", (v) -> borrowerProfile.getRegistrationAddress());
                put("borrowerResidenceAddress", (v) -> borrowerProfile.getResidenceAddress());
                put("borrowerPassportIssuedDate", (v) -> (Objects.nonNull(borrowerProfile.getPassportIssuedDate())) ? borrowerProfile.getPassportIssuedDate().toString() : "-");
                put("borrowerPassportIssuedByName", (v) -> borrowerProfile.getPassportIssuedByName());
                put("borrowerEmail", (v) -> borrowerProfile.getEmail());
                put("borrowerGender", (v) -> (Objects.nonNull(borrowerProfile.getGender())) ? borrowerProfile.getGender().getName() : "-");
                put("borrowerLastName", (v) -> borrowerProfile.getLastName());
                put("borrowerFirstName", (v) -> borrowerProfile.getFirstName());
                put("borrowerMiddleName", (v) -> borrowerProfile.getMiddleName());
                put("borrowerFIO", (v) -> borrowerProfile.getLastName() + " " + borrowerProfile.getFirstName() + " " + borrowerProfile.getMiddleName());
                put("creditTermInYears", (v) -> (Objects.nonNull(maxMonthCreditTerm) ? String.valueOf(maxMonthCreditTerm.getAsInt()) : "-"));
                put("realEstatePrice", (v) -> (Objects.nonNull(bankApplication.getRealEstatePrice())) ? bankApplication.getRealEstatePrice().toString() : "-");
                put("borrowerPassportIssuedByCode", (v) -> borrowerProfile.getPassportIssuedByCode());
                put("borrowerSNILS", (v) -> borrowerProfile.getSnils());
                put("downPayment", (v) -> (Objects.nonNull(bankApplication.getDownPayment())) ? bankApplication.getDownPayment().toString() : "-");
                put("borrowerBirthdate", (v) -> (Objects.nonNull(borrowerProfile.getBirthdate())) ? borrowerProfile.getBirthdate().toString() : "-");
                put("borrowerPrevFullName", (v) -> borrowerProfile.getPrevFullName());
                put("borrowerPhone", (v) -> borrowerProfile.getPhoneNumber());
                put("borrowerCompanyExistence", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getWorkExperience().getName() : "-");
                put("borrowerCompanyPhoneNumber", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getPhone() : "-");
                put("borrowerCompanySite", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getSite() : "-");
                put("borrowerCompanyAddress", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getAddress() : "-");
                put("borrowerCompanyPosition", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getPosition() : "-");
                put("borrowerCompanyWorkExperience", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getWorkExperience().getName() : "-");
                put("borrowerVehicleModel", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getModel() : "-");
                put("borrowerVehicleYearOfManufacture", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getYearOfManufacture().toString() : "-");
                put("borrowerVehicleBasisOfOwnership", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getBasisOfOwnership().getName() : "-");
                put("borrowerVehiclePrice", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getPrice().toString() : "-");
                put("borrowerVehicleIsCollateral", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? (borrowerProfile.getVehicle().getIsCollateral() ? "да" : "нет") : "-");
                put("borrowerRealEstateType", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getType().getName() : "-");
                put("borrowerRealEstateBasisOfOwnership", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getBasisOfOwnership().getName() : "-");
                put("borrowerRealEstateArea", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getArea().toString() : "-");
                put("borrowerRealEstatePrice", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getPrice().toString() : "-");
                put("borrowerRealEstateShare", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getShare().toString() : "-");
                put("borrowerRealEstateAddress", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getAddress() : "-");
                put("borrowerRealEstateIsCollateral", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getIsCollateral() ? "да" : "нет" : "-");
                put("borrowerIncomeVerification", (v) -> borrowerProfile.getPassportNumber());
                put("contractDate", (v) -> LocalDate.now().toString());
            }
        };
        return
                getReplaceFields(fields, fieldMapping);
    }

    private static Map<String, String> getReplaceFields
            (Set<String> fields, Map<String, Function<Void, String>> fieldMapping) {
        Map<String, String> replaceFields = new HashMap<>();
        for (String field : fields) {
            Function<Void, String> function = fieldMapping.get(field);
            if (function != null) {
                String value = function.apply(null);
                if (value != null) {
                    replaceFields.put(field, value);
                }
            }
        }
        return replaceFields;
    }

    public Set<String> extractFieldsFromDocx(byte[] file) {
        WordprocessingMLPackage wordMLPackage;
        try {
            wordMLPackage = WordprocessingMLPackage.load(new ByteArrayInputStream(file));
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        DocxFieldExtractor extractor = new DocxFieldExtractor();
        new TraversalUtil(documentPart.getContent(), extractor);
        return extractor.getFields();
    }
}

