package pro.mbroker.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.enums.BasisOfOwnership;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.Education;
import pro.mbroker.api.enums.EmploymentStatus;
import pro.mbroker.api.enums.FamilyRelation;
import pro.mbroker.api.enums.Gender;
import pro.mbroker.api.enums.MaritalStatus;
import pro.mbroker.api.enums.MarriageContract;
import pro.mbroker.api.enums.NumberOfEmployees;
import pro.mbroker.api.enums.OrganizationAge;
import pro.mbroker.api.enums.ProofOfIncome;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.api.enums.RegistrationType;
import pro.mbroker.api.enums.TotalWorkExperience;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.util.Converter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BorrowerProfileServiceTest extends BaseServiceTest {
    @Autowired
    private BorrowerProfileService borrowerProfileService;
    @Autowired
    private TestData testData;
    private static final UUID borrowerId = UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003");
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUpdateGenericBorrowerProfile() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
        BorrowerRequest borrowerRequest = new BorrowerRequest()
                .setMainBorrower(testData.getBorrowerProfileRequestList().get(0)
                        .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003")))
                .setId(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));

        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateGenericBorrowerProfile(borrowerRequest, mockRequest, 1234);
        orUpdateGenericBorrowerApplication.getCoBorrower().forEach(profile -> Assert.assertTrue(Objects.nonNull(profile.getLink())));
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("TestFirstName"));
        assertThat(mainBorrower.getLastName(), Matchers.is("TestLastName"));
    }

    @Test
    public void testUpdateBorrowerProfile() {
        BorrowerRequest borrowerRequest = new BorrowerRequest()
                .setMainBorrower(testData.getBorrowerProfileRequestList().get(0)
                        .setId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003")))
                .setId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"));
        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.createOrUpdateBorrowerProfile(borrowerRequest, 1234);
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("TestFirstName"));
        assertThat(mainBorrower.getLastName(), Matchers.is("TestLastName"));
    }

    @Test
    public void testUpdateBorrowerProfile2() {
        BorrowerResponse orUpdateGenericBorrowerApplication = borrowerProfileService.getBorrowersByPartnerApplicationId(UUID.fromString("5ff4b32c-f967-4cb1-8705-7470a321fe34"));
        BorrowerProfileResponse mainBorrower = orUpdateGenericBorrowerApplication.getMainBorrower();
        assertThat(mainBorrower.getEmail(), Matchers.is("test@test.com"));
        assertThat(mainBorrower.getFirstName(), Matchers.is("Ivan"));
        assertThat(mainBorrower.getLastName(), Matchers.is("Ivanov Perviy"));
        assertThat(mainBorrower.getMiddleName(), Matchers.is("Ivanovich"));
        assertThat(mainBorrower.getPhoneNumber(), Matchers.is("9876543219"));
    }

    @SneakyThrows
    @Test
    public void testUpdateBorrowerProfileFieldsSequentially() {
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("additionalIncome", 10000, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("age", 35, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("birthdate", "1990-01-01", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("children", 2, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("educations", List.of("INCOMPLETE_SECONDARY", "AVERAGE"), "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("email", "email@test.com", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("employmentStatus", "EMPLOYEE", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("firstName", "test_first_name", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("gender", "MALE", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("lastName", "test_last_name", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("mainIncome", 100000, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("maritalStatus", "MARRIED", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("marriageContract", "NO", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("middleName", "test_middle_name", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("passportIssuedByCode", "123123", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("passportIssuedByName", "Отделение Тестового УВД", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("passportIssuedDate", "2020-01-01", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("passportNumber", "11112222", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("pension", 10000, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("phoneNumber", "9999999999", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("prevFullName", "Тестовый Тестослав Тестович", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("proofOfIncome", "TWO_NDFL", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("registrationAddress", "test_address", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("registrationType", "PERMANENT", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("residenceAddress", "test_address_residence", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("residenceRF", true, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("snils", "8877665544", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("tin", "4444333332", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("totalWorkExperience", "FROM_1_TO_3", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("familyRelation", "HUSBAND_WIFE", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("residencyOutsideRU", "Germany", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("longTermStayOutsideRU", "Germany", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("isPublicOfficial", true, "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("relatedPublicOfficial", "HUSBAND_WIFE", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("taxResidencyCountries", "Test_Counties", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("publicOfficialPosition", "test_important_employer", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("tinForeign", "4455000002", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("birthPlace", "test_birth_place", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, Map.of("citizenship", "test_citizenship_counties", "sdId", 1234));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"address\": \"test_address\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"branch\": \"RESCUE\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"tin\": \"555666677788\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"name\": \"test_name_employer\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"numberOfEmployees\": \"LESS_THAN_10\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"organizationAge\": \"LESS_THAN_1\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"phone\": \"4556667778\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"position\": \"director\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"site\": \"site.com\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"workExperience\": \"FROM_1_TO_3\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"isCurrentEmployer\": true}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"bankDetails\": \"test_bank_details\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"manager\": \"test_manager_contact\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"employer\": {\"manager\": \"test_manager_contact\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"address\": \"test_address\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"area\": 100}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"basisOfOwnership\": \"PURCHASE\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"isCollateral\": true}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"price\": 10000000}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"share\": 50}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"realEstate\": {\"type\": \"APARTMENT\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"vehicle\": {\"basisOfOwnership\": \"PURCHASE\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"vehicle\": {\"isCollateral\": true}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"vehicle\": {\"model\": \"LADA_BAKLAZHAN\"}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"vehicle\": {\"price\": 1000000}, \"sdId\": 1234}", new TypeReference<>() {
        }));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, new ObjectMapper().readValue("{\"vehicle\": {\"yearOfManufacture\": 2000}, \"sdId\": 1234}", new TypeReference<>() {
        }));

        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertEquals(10000, borrowerProfile.getAdditionalIncome());
        assertEquals(35, borrowerProfile.getAge());
        assertEquals(LocalDate.of(1990, 1, 1), borrowerProfile.getBirthdate());
        assertEquals(2, borrowerProfile.getChildren());
        assertEquals(Converter.convertStringListToEnumList(borrowerProfile.getEducations(), Education.class), List.of(Education.INCOMPLETE_SECONDARY, Education.AVERAGE));
        assertEquals("email@test.com", borrowerProfile.getEmail());
        assertEquals(EmploymentStatus.EMPLOYEE, borrowerProfile.getEmploymentStatus());
        assertEquals("test_first_name", borrowerProfile.getFirstName());
        assertEquals(Gender.MALE, borrowerProfile.getGender());
        assertEquals("test_last_name", borrowerProfile.getLastName());
        assertEquals(100000, borrowerProfile.getMainIncome());
        assertEquals(MaritalStatus.MARRIED, borrowerProfile.getMaritalStatus());
        assertEquals(MarriageContract.NO, borrowerProfile.getMarriageContract());
        assertEquals("test_middle_name", borrowerProfile.getMiddleName());
        assertEquals("123123", borrowerProfile.getPassportIssuedByCode());
        assertEquals("Отделение Тестового УВД", borrowerProfile.getPassportIssuedByName());
        assertEquals(LocalDate.of(2020, 1, 1), borrowerProfile.getPassportIssuedDate());
        assertEquals("11112222", borrowerProfile.getPassportNumber());
        assertEquals(10000, borrowerProfile.getPension());
        assertEquals("9999999999", borrowerProfile.getPhoneNumber());
        assertEquals("Тестовый Тестослав Тестович", borrowerProfile.getPrevFullName());
        assertEquals(ProofOfIncome.TWO_NDFL, borrowerProfile.getProofOfIncome());
        assertEquals("test_address_residence", borrowerProfile.getResidenceAddress());
        assertEquals(RegistrationType.PERMANENT, borrowerProfile.getRegistrationType());
        assertEquals("test_address_residence", borrowerProfile.getResidenceAddress());
        assertEquals(true, borrowerProfile.getResidenceRF());
        assertEquals("8877665544", borrowerProfile.getSnils());
        assertEquals(TotalWorkExperience.FROM_1_TO_3, borrowerProfile.getTotalWorkExperience());
        assertEquals("4444333332", borrowerProfile.getTin());
        assertEquals(FamilyRelation.HUSBAND_WIFE, borrowerProfile.getFamilyRelation());
        assertEquals("Germany", borrowerProfile.getResidencyOutsideRU());
        assertEquals("Germany", borrowerProfile.getLongTermStayOutsideRU());
        assertEquals(true, borrowerProfile.getIsPublicOfficial());
        assertEquals(FamilyRelation.HUSBAND_WIFE, borrowerProfile.getRelatedPublicOfficial());
        assertEquals("Test_Counties", borrowerProfile.getTaxResidencyCountries());
        assertEquals("test_important_employer", borrowerProfile.getPublicOfficialPosition());
        assertEquals("4455000002", borrowerProfile.getTinForeign());
        assertEquals("test_birth_place", borrowerProfile.getBirthPlace());
        assertEquals("test_citizenship_counties", borrowerProfile.getCitizenship());
        assertEquals("test_address", borrowerProfile.getEmployer().getAddress());
        assertEquals(Branch.RESCUE, borrowerProfile.getEmployer().getBranch());
        assertEquals("555666677788", borrowerProfile.getEmployer().getTin());
        assertEquals("test_name_employer", borrowerProfile.getEmployer().getName());
        assertEquals(NumberOfEmployees.LESS_THAN_10, borrowerProfile.getEmployer().getNumberOfEmployees());
        assertEquals(OrganizationAge.LESS_THAN_1, borrowerProfile.getEmployer().getOrganizationAge());
        assertEquals("4556667778", borrowerProfile.getEmployer().getPhone());
        assertEquals("director", borrowerProfile.getEmployer().getPosition());
        assertEquals("site.com", borrowerProfile.getEmployer().getSite());
        assertEquals(TotalWorkExperience.FROM_1_TO_3, borrowerProfile.getEmployer().getWorkExperience());
        assertEquals(true, borrowerProfile.getEmployer().getIsCurrentEmployer());
        assertEquals("test_bank_details", borrowerProfile.getEmployer().getBankDetails());
        assertEquals("test_manager_contact", borrowerProfile.getEmployer().getManager());
        assertEquals("test_address", borrowerProfile.getRealEstate().getAddress());
        assertEquals(100, borrowerProfile.getRealEstate().getArea());
        assertEquals(BasisOfOwnership.PURCHASE, borrowerProfile.getRealEstate().getBasisOfOwnership());
        assertEquals(true, borrowerProfile.getRealEstate().getIsCollateral());
        assertEquals(10000000, borrowerProfile.getRealEstate().getPrice());
        assertEquals(50, borrowerProfile.getRealEstate().getShare());
        assertEquals(RealEstateType.APARTMENT, borrowerProfile.getRealEstate().getType());
        assertEquals(BasisOfOwnership.PURCHASE, borrowerProfile.getVehicle().getBasisOfOwnership());
        assertEquals(true, borrowerProfile.getVehicle().getIsCollateral());
        assertEquals("LADA_BAKLAZHAN", borrowerProfile.getVehicle().getModel());
        assertEquals(1000000, borrowerProfile.getVehicle().getPrice());
        assertEquals(2000, borrowerProfile.getVehicle().getYearOfManufacture());
    }

    @Test
    public void testDeleteBorrowerProfileFieldsSequentially() {

        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("additionalIncome", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("age", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("birthdate", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("children", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("educations", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("email", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("employmentStatus", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("firstName", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("gender", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("lastName", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("mainIncome", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("maritalStatus", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("marriageContract", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("middleName", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("passportIssuedByCode", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("passportIssuedByName", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("passportIssuedDate", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("passportNumber", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("pension", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("phoneNumber", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("prevFullName", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("proofOfIncome", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("registrationAddress", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("registrationType", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("residenceAddress", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("residenceRF", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("snils", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("tin", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("totalWorkExperience", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("familyRelation", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("residencyOutsideRU", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("longTermStayOutsideRU", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("isPublicOfficial", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("relatedPublicOfficial", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("taxResidencyCountries", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("publicOfficialPosition", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("tinForeign", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("birthPlace", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("citizenship", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMap("citizenship", null));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"address\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"branch\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"tin\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"name\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"numberOfEmployees\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"organizationAge\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"phone\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"position\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"site\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"workExperience\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"isCurrentEmployer\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"bankDetails\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"employer\": {\"manager\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"address\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"area\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"basisOfOwnership\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"isCollateral\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"price\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"share\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"realEstate\": {\"type\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"vehicle\": {\"basisOfOwnership\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"vehicle\": {\"isCollateral\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"vehicle\": {\"model\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"vehicle\": {\"price\": null}}"));
        borrowerProfileService.updateBorrowerProfileField(borrowerId, getFieldMapFromJson("{\"vehicle\": {\"yearOfManufacture\": null}}"));

        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerId);
        assertNull(borrowerProfile.getAdditionalIncome());
        assertNull(borrowerProfile.getAge());
        assertNull(borrowerProfile.getBirthdate());
        assertNull(borrowerProfile.getChildren());
        assertNull(borrowerProfile.getEducations());
        assertNull(borrowerProfile.getEmail());
        assertNull(borrowerProfile.getEmploymentStatus());
        assertNull(borrowerProfile.getFirstName());
        assertNull(borrowerProfile.getGender());
        assertNull(borrowerProfile.getLastName());
        assertNull(borrowerProfile.getMainIncome());
        assertNull(borrowerProfile.getMaritalStatus());
        assertNull(borrowerProfile.getMarriageContract());
        assertNull(borrowerProfile.getMiddleName());
        assertNull(borrowerProfile.getPassportIssuedByCode());
        assertNull(borrowerProfile.getPassportIssuedByName());
        assertNull(borrowerProfile.getPassportIssuedDate());
        assertNull(borrowerProfile.getPassportNumber());
        assertNull(borrowerProfile.getPension());
        assertNull(borrowerProfile.getPhoneNumber());
        assertNull(borrowerProfile.getPrevFullName());
        assertNull(borrowerProfile.getProofOfIncome());
        assertNull(borrowerProfile.getResidenceAddress());
        assertNull(borrowerProfile.getRegistrationType());
        assertNull(borrowerProfile.getResidenceRF());
        assertNull(borrowerProfile.getSnils());
        assertNull(borrowerProfile.getTotalWorkExperience());
        assertNull(borrowerProfile.getFamilyRelation());
        assertNull(borrowerProfile.getResidencyOutsideRU());
        assertNull(borrowerProfile.getLongTermStayOutsideRU());
        assertNull(borrowerProfile.getIsPublicOfficial());
        assertNull(borrowerProfile.getRelatedPublicOfficial());
        assertNull(borrowerProfile.getTaxResidencyCountries());
        assertNull(borrowerProfile.getPublicOfficialPosition());
        assertNull(borrowerProfile.getTinForeign());
        assertNull(borrowerProfile.getBirthPlace());
        assertNull(borrowerProfile.getCitizenship());
        assertNull(borrowerProfile.getEmployer().getAddress());
        assertNull(borrowerProfile.getEmployer().getBranch());
        assertNull(borrowerProfile.getEmployer().getTin());
        assertNull(borrowerProfile.getEmployer().getName());
        assertNull(borrowerProfile.getEmployer().getNumberOfEmployees());
        assertNull(borrowerProfile.getEmployer().getOrganizationAge());
        assertNull(borrowerProfile.getEmployer().getPhone());
        assertNull(borrowerProfile.getEmployer().getPosition());
        assertNull(borrowerProfile.getEmployer().getSite());
        assertNull(borrowerProfile.getEmployer().getWorkExperience());
        assertNull(borrowerProfile.getEmployer().getIsCurrentEmployer());
        assertNull(borrowerProfile.getEmployer().getBankDetails());
        assertNull(borrowerProfile.getEmployer().getManager());
        assertNull(borrowerProfile.getRealEstate().getAddress());
        assertNull(borrowerProfile.getRealEstate().getArea());
        assertNull(borrowerProfile.getRealEstate().getBasisOfOwnership());
        assertNull(borrowerProfile.getRealEstate().getIsCollateral());
        assertNull(borrowerProfile.getRealEstate().getPrice());
        assertNull(borrowerProfile.getRealEstate().getShare());
        assertNull(borrowerProfile.getRealEstate().getType());
        assertNull(borrowerProfile.getVehicle().getBasisOfOwnership());
        assertNull(borrowerProfile.getVehicle().getIsCollateral());
        assertNull(borrowerProfile.getVehicle().getModel());
        assertNull(borrowerProfile.getVehicle().getPrice());
        assertNull(borrowerProfile.getVehicle().getYearOfManufacture());
    }

    private Map<String, Object> getFieldMap(String fieldName, Objects value) {
        Map<String, Object> map = new HashMap<>() {{
            put(fieldName, value);
            put("sdId", 1234);
        }};
        return map;
    }

    private Map<String, Object> getFieldMapFromJson(String json) {
        Map<String, Object> fieldMap;
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
            };
            fieldMap = objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
        fieldMap.put("sdId", 1234);
        return fieldMap;
    }
}
