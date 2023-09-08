package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.TraversalUtil;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerEmployer;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.service.DocxFieldHandler;
import pro.mbroker.app.util.DocxFieldExtractor;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocxFieldHandlerImpl implements DocxFieldHandler {

    public Map<String, String> replaceFieldValue(MultipartFile file, BankApplication bankApplication, BorrowerProfile borrowerProfile) {
        Set<String> fields;
        try {
            fields = extractFieldsFromDocx(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Map<String, Function<Void, String>> fieldMapping = new HashMap<>() {
            {
                put("borrowerPassportNumber", (v) -> borrowerProfile.getPassportNumber());
                put("borrowerType", (v) -> bankApplication.getMainBorrower().getId().equals(borrowerProfile.getId()) ? "Заемщик" : "Созаемщик");
                put("borrowerEducation", (v) -> borrowerProfile.getEducation().getName());
                put("borrowerTotalWorkExperience", (v) -> borrowerProfile.getTotalWorkExperience().getName());
                put("borrowerMarriageContract", (v) -> borrowerProfile.getMarriageContract() ? "да" : "нет");
                put("borrowerEmploymentStatus", (v) -> borrowerProfile.getEmploymentStatus().getName());
                put("borrowerChildren", (v) -> borrowerProfile.getChildren().toString());
                put("borrowerMaritalStatus", (v) -> borrowerProfile.getMaritalStatus().getName());
                put("borrowerCompanyName", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getName).orElse(null));
                put("borrowerCompanyInn", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getInn).map(Object::toString).orElse(null));
                put("borrowerCompanyBranch", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getBranch).map(Branch::getName).orElse(null));
                put("borrowerCompanyEmployeeCount", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getNumberOfEmployees).map(NumberOfEmployees::getName).orElse(null));
                put("creditAmount", (v) -> bankApplication.getRealEstatePrice().subtract(bankApplication.getDownPayment()).toString());
                put("creditPurposeType", (v) -> CreditPurposeType.valueOf(bankApplication.getCreditProgram().getCreditProgramDetail().getCreditPurposeType()).getName());
                put("realEstateType", (v) -> RealEstateType.valueOf(bankApplication.getCreditProgram().getCreditProgramDetail().getRealEstateType()).getName());
                put("realEstateRegion", (v) -> bankApplication.getPartnerApplication().getRealEstate().getRegion().getName());
                put("borrowerRegistrationAddress", (v) -> borrowerProfile.getRegistrationAddress());
                put("borrowerResidenceAddress", (v) -> borrowerProfile.getResidenceAddress());
                put("borrowerPassportIssuedDate", (v) -> borrowerProfile.getPassportIssuedDate().toString());
                put("borrowerPassportIssuedByName", (v) -> borrowerProfile.getPassportIssuedByName());
                put("borrowerEmail", (v) -> borrowerProfile.getEmail());
                put("borrowerGender", (v) -> borrowerProfile.getGender().getName());
                put("borrowerLastName", (v) -> borrowerProfile.getLastName());
                put("borrowerFirstName", (v) -> borrowerProfile.getFirstName());
                put("borrowerMiddleName", (v) -> borrowerProfile.getMiddleName());
                put("borrowerFIO", (v) -> borrowerProfile.getLastName() + " " + borrowerProfile.getFirstName() + " " + borrowerProfile.getMiddleName());
                put("creditTermInYears", (v) -> bankApplication.getMonthCreditTerm().toString());
                put("realEstatePrice", (v) -> bankApplication.getRealEstatePrice().toString());
                put("borrowerPassportIssuedByCode", (v) -> borrowerProfile.getPassportIssuedByCode());
                put("borrowerSNILS", (v) -> borrowerProfile.getSnils());
                put("downPayment", (v) -> bankApplication.getDownPayment().toString());
                put("borrowerBirthdate", (v) -> borrowerProfile.getBirthdate().toString());
                put("borrowerPrevFullName", (v) -> borrowerProfile.getPrevFullName());
                put("borrowerPhone", (v) -> borrowerProfile.getPhoneNumber());
                put("borrowerCompanyExistence", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getWorkExperience().getName() : null);
                put("borrowerCompanyPhoneNumber", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getPhone() : null);
                put("borrowerCompanySite", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getSite() : null);
                put("borrowerCompanyAddress", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getAddress() : null);
                put("borrowerCompanyPosition", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getPosition() : null);
                put("borrowerCompanyWorkExperience", (v) -> Objects.nonNull(borrowerProfile.getEmployer()) ? borrowerProfile.getEmployer().getWorkExperience().getName() : null);
                put("borrowerVehicleModel", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getModel() : null);
                put("borrowerVehicleYearOfManufacture", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getYearOfManufacture().toString() : null);
                put("borrowerVehicleBasisOfOwnership", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getBasisOfOwnership().getName() : null);
                put("borrowerVehiclePrice", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? borrowerProfile.getVehicle().getPrice().toString() : null);
                put("borrowerVehicleIsCollateral", (v) -> Objects.nonNull(borrowerProfile.getVehicle()) ? (borrowerProfile.getVehicle().getIsCollateral() ? "да" : "нет") : null);
                put("borrowerRealEstateType", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getType().getName() : null);
                put("borrowerRealEstateBasisOfOwnership", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getBasisOfOwnership().getName() : null);
                put("borrowerRealEstateArea", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getArea().toString() : null);
                put("borrowerRealEstatePrice", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getPrice().toString() : null);
                put("borrowerRealEstateShare", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getShare().toString() : null);
                put("borrowerRealEstateAddress", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getAddress() : null);
                put("borrowerRealEstateIsCollateral", (v) -> Objects.nonNull(borrowerProfile.getRealEstate()) ? borrowerProfile.getRealEstate().getIsCollateral() ? "да" : "нет" : null);
                put("borrowerIncomeVerification", (v) -> borrowerProfile.getPassportNumber());
                put("contractDate", (v) -> LocalDate.now().toString());
            }
        };
        return getReplaceFields(fields, fieldMapping);
    }

    private static Map<String, String> getReplaceFields(Set<String> fields, Map<String, Function<Void, String>> fieldMapping) {
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

    public Set<String> extractFieldsFromDocx(MultipartFile file) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new ByteArrayInputStream(file.getBytes()));
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        DocxFieldExtractor extractor = new DocxFieldExtractor();
        new TraversalUtil(documentPart.getContent(), extractor);
        return extractor.getFields();
    }
}

