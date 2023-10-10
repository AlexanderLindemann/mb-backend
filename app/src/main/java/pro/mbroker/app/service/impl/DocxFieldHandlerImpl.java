package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.TraversalUtil;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Service;
import pro.mbroker.api.enums.BasisOfOwnership;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.Education;
import pro.mbroker.api.enums.Gender;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.TotalWorkExperience;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerEmployer;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.DocxFieldHandler;
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
                put("borrowerPassportNumber", (v) -> (Objects.nonNull(borrowerProfile.getPassportNumber())
                        ? borrowerProfile.getPassportNumber()
                        : "-"));
                put("borrowerType", (v) -> (Objects.nonNull(mainBorrower))
                        ? mainBorrower.getId().equals(borrowerProfile.getId())
                        ? "Заемщик" : "Созаемщик" : "-");
                put("borrowerEducation", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(BorrowerProfile::getEducation)
                        .map(Education::getName)
                        .orElse("-"));

                put("borrowerTotalWorkExperience", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(BorrowerProfile::getTotalWorkExperience)
                        .map(TotalWorkExperience::getName)
                        .orElse("-"));

                put("borrowerMarriageContract", (v) -> (Objects.nonNull(borrowerProfile.getMarriageContract()) ? borrowerProfile.getMarriageContract().getName() : "-"));
                put("borrowerEmploymentStatus", (v) -> (Objects.nonNull(borrowerProfile.getEmploymentStatus())) ? borrowerProfile.getEmploymentStatus().getName() : "-");
                put("borrowerChildren", (v) -> (Objects.nonNull(borrowerProfile.getChildren())) ? borrowerProfile.getChildren().toString() : "-");
                put("borrowerMaritalStatus", (v) -> (Objects.nonNull(borrowerProfile.getMaritalStatus())) ? borrowerProfile.getMaritalStatus().getName() : "-");
                put("borrowerCompanyName", (v) -> Optional.ofNullable(borrowerProfile.getEmployer())
                        .map(employer -> Optional.ofNullable(employer.getName()).orElse("-"))
                        .orElse("-"));
                put("borrowerCompanyInn", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getInn).map(Object::toString).orElse("-"));
                put("borrowerCompanyBranch", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getBranch).map(Branch::getName).orElse("-"));
                put("borrowerCompanyEmployeeCount", (v) -> Optional.ofNullable(borrowerProfile.getEmployer()).map(BorrowerEmployer::getNumberOfEmployees).map(NumberOfEmployees::getName).orElse("-"));
                put("creditAmount", (v) -> {
                    if (Objects.nonNull(bankApplication)
                            && Objects.nonNull(bankApplication.getRealEstatePrice())
                            && Objects.nonNull(bankApplication.getDownPayment())) {
                        return bankApplication.getRealEstatePrice().subtract(bankApplication.getDownPayment()).toString();
                    } else {
                        return "-";
                    }
                });

                put("realEstateType", (v) -> Optional.ofNullable(partnerApplication)
                        .map(PartnerApplication::getRealEstateType)
                        .map(RealEstateType::getName)
                        .orElse("-"));

                put("creditPurposeType", (v) -> Objects.nonNull(partnerApplication.getCreditPurposeType()) ? partnerApplication.getCreditPurposeType().getName() : "-");

                put("realEstateRegion", (v) -> {
                    if (Objects.nonNull(bankApplication) &&
                            Objects.nonNull(bankApplication.getPartnerApplication()) &&
                            Objects.nonNull(bankApplication.getPartnerApplication().getRealEstate()) &&
                            Objects.nonNull(bankApplication.getPartnerApplication().getRealEstate().getRegion())) {
                        return bankApplication.getPartnerApplication().getRealEstate().getRegion().getName();
                    } else {
                        return "-";
                    }
                });
                put("borrowerRegistrationAddress", (v) -> Optional.ofNullable(borrowerProfile.getRegistrationAddress())
                        .map(String::toString)
                        .orElse("-"));

                put("borrowerResidenceAddress", (v) -> Optional.ofNullable(borrowerProfile.getResidenceAddress())
                        .map(String::toString)
                        .orElse("-"));

                put("borrowerPassportIssuedDate", (v) -> Optional.ofNullable(borrowerProfile.getPassportIssuedDate())
                        .map(LocalDate::toString)
                        .orElse("-"));

                put("borrowerPassportIssuedByName", (v) -> Optional.ofNullable(borrowerProfile.getPassportIssuedByName())
                        .orElse("-"));

                put("borrowerEmail", (v) -> Optional.ofNullable(borrowerProfile.getEmail())
                        .orElse("-"));

                put("borrowerGender", (v) -> Optional.ofNullable(borrowerProfile.getGender())
                        .map(Gender::getName)
                        .orElse("-"));
                put("borrowerLastName", (v) -> Optional.ofNullable(borrowerProfile.getLastName())
                        .orElse("-"));

                put("borrowerFirstName", (v) -> Optional.ofNullable(borrowerProfile.getFirstName())
                        .orElse("-"));

                put("borrowerMiddleName", (v) -> Optional.ofNullable(borrowerProfile.getMiddleName())
                        .orElse("-"));

                put("borrowerFIO", (v) -> {
                    StringBuilder fio = new StringBuilder();
                    fio.append(Optional.ofNullable(borrowerProfile.getLastName()).orElse("")).append("  ")
                            .append(Optional.ofNullable(borrowerProfile.getFirstName()).orElse("")).append("  ")
                            .append(Optional.ofNullable(borrowerProfile.getMiddleName()).orElse(""));

                    return fio.length() < 1 ? "-" : fio.toString();
                });

                put("creditTermInYears", (v) -> Optional.ofNullable(maxMonthCreditTerm)
                        .map(value -> String.valueOf(value.getAsInt() / 12))
                        .orElse("-"));


                put("realEstatePrice", (v) -> Objects.nonNull(bankApplication)
                        && Objects.nonNull(bankApplication.getRealEstatePrice())
                        ? bankApplication.getRealEstatePrice().toString()
                        : "-");

                put("borrowerPassportIssuedByCode", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getPassportIssuedByCode())
                        ? borrowerProfile.getPassportIssuedByCode()
                        : "-");

                put("borrowerSNILS", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getSnils())
                        ? borrowerProfile.getSnils()
                        : "-");

                put("downPayment", (v) -> Optional.ofNullable(bankApplication)
                        .map(app -> Optional.ofNullable(app.getDownPayment())
                                .map(Object::toString)
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerBirthdate", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getBirthdate())
                                .map(Object::toString)
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerPrevFullName", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getPrevFullName())
                        ? borrowerProfile.getPrevFullName()
                        : "-");

                put("borrowerPhone", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getPhoneNumber())
                        ? borrowerProfile.getPhoneNumber()
                        : "-");

                put("borrowerCompanyExistence", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getEmployer())
                                .map(employer -> Optional.ofNullable(employer.getWorkExperience())
                                        .map(TotalWorkExperience::getName)
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerCompanyPhoneNumber", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getEmployer())
                                .map(BorrowerEmployer::getPhone)
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerCompanySite", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getEmployer())
                                .map(BorrowerEmployer::getSite)
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerCompanyAddress", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getEmployer())
                                .map(BorrowerEmployer::getAddress)
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerCompanyPosition", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getEmployer())
                                .map(BorrowerEmployer::getPosition)
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerCompanyWorkExperience", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getEmployer())
                                .map(employer -> Optional.ofNullable(employer.getWorkExperience())
                                        .map(TotalWorkExperience::getName)
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerVehicleModel", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getVehicle())
                                .map(vehicle -> Optional.ofNullable(vehicle.getModel())
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerVehicleYearOfManufacture", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getVehicle())
                                .map(vehicle -> Optional.ofNullable(vehicle.getYearOfManufacture())
                                        .map(Object::toString)
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerVehicleBasisOfOwnership", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getVehicle())
                                .map(vehicle -> Optional.ofNullable(vehicle.getBasisOfOwnership())
                                        .map(BasisOfOwnership::getName)
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerVehiclePrice", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getVehicle())
                                .map(vehicle -> Optional.ofNullable(vehicle.getPrice())
                                        .map(Object::toString)
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerVehicleIsCollateral", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getVehicle())
                                .map(vehicle -> vehicle.getIsCollateral() ? "да" : "нет")
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerRealEstateType", (v) -> Optional.ofNullable(borrowerProfile)
                        .map(profile -> Optional.ofNullable(profile.getRealEstate())
                                .map(realEstate -> Optional.ofNullable(realEstate.getType())
                                        .map(RealEstateType::getName)
                                        .orElse("-"))
                                .orElse("-"))
                        .orElse("-"));

                put("borrowerRealEstateBasisOfOwnership", (v) -> (Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getRealEstate())
                        && Objects.nonNull(borrowerProfile.getRealEstate().getBasisOfOwnership()))
                        ? borrowerProfile.getRealEstate().getBasisOfOwnership().getName()
                        : "-");

                put("borrowerRealEstateShare", (v) -> (Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getRealEstate())
                        && Objects.nonNull(borrowerProfile.getRealEstate().getShare()))
                        ? borrowerProfile.getRealEstate().getShare().toString()
                        : "-");

                put("borrowerRealEstateAddress", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getRealEstate())
                        ? borrowerProfile.getRealEstate().getAddress()
                        : "-");

                put("borrowerRealEstateIsCollateral", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getRealEstate())
                        ? (borrowerProfile.getRealEstate().getIsCollateral()
                        ? "да" : "нет") : "-");

                put("borrowerIncomeVerification", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getProofOfIncome())
                        ? borrowerProfile.getProofOfIncome().getName()
                        : "-");

                put("contractDate", (v) -> LocalDate.now().toString());

                put("borrowerTIN", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getTIN())
                        ? borrowerProfile.getTIN().toString()
                        : "-");

                put("borrowerResidencyOutsideRU", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getResidencyOutsideRU())
                        ? borrowerProfile.getResidencyOutsideRU()
                        : "-");

                put("borrowerLongTermStayOutsideRU", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getLongTermStayOutsideRU())
                        ? borrowerProfile.getLongTermStayOutsideRU()
                        : "-");

                put("realEstateResidentialComplexName", (v) -> Objects.nonNull(partnerApplication.getRealEstate())
                        ? partnerApplication.getRealEstate().getResidentialComplexName()
                        : "-");

                put("borrowerCompanyManager", (v) -> Objects.nonNull(borrowerProfile.getEmployer())
                        && Objects.nonNull(borrowerProfile.getEmployer().getManager())
                        ? borrowerProfile.getEmployer().getManager()
                        : "-");

                put("borrowerCompanyBankDetails", (v) -> Objects.nonNull(borrowerProfile.getEmployer())
                        && Objects.nonNull(borrowerProfile.getEmployer().getBankDetails())
                        ? borrowerProfile.getEmployer().getBankDetails()
                        : "-");

                put("borrowerMainIncome", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getMainIncome())
                        ? borrowerProfile.getMainIncome().toString()
                        : "-");

                put("borrowerAdditionalIncome", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getAdditionalIncome())
                        ? borrowerProfile.getAdditionalIncome().toString()
                        : "-");

                put("borrowerPension", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getPension())
                        ? borrowerProfile.getPension().toString()
                        : "-");

                put("borrowerRealEstateArea", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getRealEstate())
                        && Objects.nonNull(borrowerProfile.getRealEstate().getArea())
                        ? borrowerProfile.getRealEstate().getArea().toString()
                        : "-");

                put("borrowerRealEstatePrice", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getRealEstate())
                        && Objects.nonNull(borrowerProfile.getRealEstate().getPrice())
                        ? borrowerProfile.getRealEstate().getPrice().toString()
                        : "-");

                put("borrowerCompanySalaryBank", (v) -> {
                    if (Objects.nonNull(borrowerProfile.getEmployer()) && Objects.nonNull(borrowerProfile.getEmployer().getSalaryBanks())) {
                        return borrowerProfile.getEmployer().getSalaryBanks().stream()
                                .map(Bank::getName)
                                .collect(Collectors.joining(", "));
                    } else {
                        return "-";
                    }
                });

                put("borrowerResidenceRF", (v) -> Objects.nonNull(borrowerProfile)
                        && Objects.nonNull(borrowerProfile.getResidenceRF())
                        ? (borrowerProfile.getResidenceRF()
                        ? "да" : "нет") : "-");

                put("creditProgramName", (v) -> {
                    if (Objects.nonNull(partnerApplication.getPartner()) && Objects.nonNull(partnerApplication.getPartner().getCreditPrograms())) {
                        return partnerApplication.getPartner().getCreditPrograms().stream()
                                .map(CreditProgram::getProgramName)
                                .collect(Collectors.joining(", "));
                    } else {
                        return "-";
                    }
                });

                //todo доработать поля, когда будет расширение БД
                put("borrowerBirthPlace", (v) -> "-");
                put("borrowerFamilyRelation", (v) -> "-");
                put("borrowerTINForeign", (v) -> "-");
                put("borrowerCitezenship", (v) -> "-");
                put("borrowerTaxResidencyCountries", (v) -> "-");
                put("borrowerIsPublicOfficial", (v) -> "-");
                put("borrowerRelatedPublicOfficial", (v) -> "-");
                put("paymentSource", (v) -> "-");
                put("maternalCapitalAmount", (v) -> "-");
                put("subsidiesAmount", (v) -> "-");
                put("insurance", (v) -> "-");
                put("borrowerCompany2Name", (v) -> "-");
                put("borrowerCompany2Inn", (v) -> "-");
                put("borrowerCompany2Branch»", (v) -> "-");
                put("borrowerCompany2EmployeeCount", (v) -> "-");
                put("borrowerCompany2Existence", (v) -> "-");
                put("borrowerCompany2PhoneNumber", (v) -> "-");
                put("borrowerCompany2Site", (v) -> "-");
                put("borrowerCompany2Address", (v) -> "-");
                put("borrowerCompany2Position", (v) -> "-");
                put("borrowerCompany2WorkExperience", (v) -> "-");
                put("borrowerCompany2SalaryBank", (v) -> "-");
                put("borrowerCompany2Manager", (v) -> "-");
                put("borrowerCompany2BankDetails", (v) -> "-");
                put("borrowerPrevCompanyName", (v) -> "-");
                put("borrowerPrevCompanyInn", (v) -> "-");
                put("borrowerPrevCompanyBranch", (v) -> "-");
                put("borrowerPrevCompanyEmployeeCount", (v) -> "-");
                put("borrowerPrevCompanyExistence", (v) -> "-");
                put("borrowerPrevCompanyPhoneNumber", (v) -> "-");
                put("borrowerPrevCompanySite", (v) -> "-");
                put("borrowerPrevCompanyAddress", (v) -> "-");
                put("borrowerPrevCompanyPosition", (v) -> "-");
                put("borrowerPrevCompanyWorkExperience", (v) -> "-");
                put("borrowerPrevCompanySalaryBank", (v) -> "-");
                put("borrowerPrevCompanyManager", (v) -> "-");
                put("borrowerPrevCompanyBankDetails", (v) -> "-");
                put("borrowerRealEstate2Type", (v) -> "-");
                put("borrowerRealEstate2BasisOfOwnership", (v) -> "-");
                put("borrowerRealEstate2Area", (v) -> "-");
                put("borrowerRealEstate2Price", (v) -> "-");
                put("borrowerRealEstate2Share", (v) -> "-");
                put("borrowerRealEstate2Address", (v) -> "-");
                put("borrowerRealEstate2IsCollateral", (v) -> "-");
                put("borrowerVehicle2Model", (v) -> "-");
                put("borrowerVehicle2YearOfManufacture", (v) -> "-");
                put("borrowerVehicle2BasisOfOwnership", (v) -> "-");
                put("borrowerVehicle2Price", (v) -> "-");
                put("borrowerVehicle2IsCollateral", (v) -> "-");
                put("borrowerCompany2Branch", (v) -> "-");

            }
        };
        return
                getReplaceFields(fieldMapping);
    }

    private static Map<String, String> getReplaceFields(Map<String, Function<Void, String>> originalMap) {
        Map<String, String> replaceFields = new HashMap<>();
        for (Map.Entry<String, Function<Void, String>> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Function<Void, String> function = entry.getValue();

            String value = function.apply(null);
            replaceFields.put(key, value);
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

