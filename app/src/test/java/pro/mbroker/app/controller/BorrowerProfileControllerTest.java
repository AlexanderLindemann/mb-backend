package pro.mbroker.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.api.enums.BasisOfOwnership;
import pro.mbroker.api.enums.Branch;
import pro.mbroker.api.enums.Education;
import pro.mbroker.api.enums.EmploymentStatus;
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
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.service.BorrowerProfileService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BorrowerProfileControllerTest extends AbstractControllerTest {
    @Autowired
    private BorrowerProfileService borrowerProfileService;

    @Autowired
    private TestData testData;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createOrUpdateBorrowerProfileTest() throws Exception {
        BorrowerRequest borrowerRequest = testData.getBorrowerRequest();
        MvcResult mvcResult = mockMvc.perform(post("/public/borrower_profile/bank_application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithoutAdminPermission)
                        .content(objectMapper.writeValueAsString(borrowerRequest.setId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566")))))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();

        assertEquals("1348b508-f476-11ed-a05b-0242ac120003", JsonPath.read(responseBody, "$.mainBorrower.id"));
        assertEquals("Ivan", JsonPath.read(responseBody, "$.mainBorrower.firstName"));
        assertEquals("Ivanov", JsonPath.read(responseBody, "$.mainBorrower.lastName"));
        assertEquals("Ivanovich", JsonPath.read(responseBody, "$.mainBorrower.middleName"));
        assertEquals("+90000000000", JsonPath.read(responseBody, "$.mainBorrower.phoneNumber"));
        assertEquals("test@test.com", JsonPath.read(responseBody, "$.mainBorrower.email"));
        List<Map<String, Object>> coBorrowerList = JsonPath.read(responseBody, "$.coBorrower");
        List<BorrowerProfileResponse> expectedCoBorrowers = List.of(
                new BorrowerProfileResponse()
                        .setFirstName("TestFirstName2").setLastName("TestLastName2").setEmail("test2@test.com"),
                new BorrowerProfileResponse()
                        .setFirstName("TestFirstName3").setLastName("TestLastName3").setEmail("test3@test.com"),
                new BorrowerProfileResponse()
                        .setFirstName("TestFirstName").setLastName("TestLastName").setEmail("test@test.com")
        );
        for (BorrowerProfileResponse expectedCoBorrower : expectedCoBorrowers) {
            boolean found = coBorrowerList.stream().anyMatch(coBorrower ->
                    expectedCoBorrower.getFirstName().equals(coBorrower.get("firstName")) &&
                            expectedCoBorrower.getLastName().equals(coBorrower.get("lastName")) &&
                            expectedCoBorrower.getEmail().equals(coBorrower.get("email")));
            assertTrue(found, "Did not find expected coBorrower: " + expectedCoBorrower);
        }
    }

    @Test
    public void testUpdateBorrowerProfileFieldsSequentially() {
        updateField("{\"additionalIncome\": 10000}");
        updateField("{\"age\": 35}");
        updateField("{\"birthdate\": \"1990-01-01\"}");
        updateField("{\"children\": 2}");
        updateField("{\"education\": \"INCOMPLETE_SECONDARY\"}");
        updateField("{\"email\": \"email@test.com\"}");
        updateField("{\"employmentStatus\": \"EMPLOYEE\"}");
        updateField("{\"firstName\": \"test_first_name\"}");
        updateField("{\"gender\": \"MALE\"}");
        updateField("{\"lastName\": \"test_last_name\"}");
        updateField("{\"mainIncome\": 100000}");
        updateField("{\"maritalStatus\": \"MARRIED\"}");
        updateField("{\"marriageContract\": \"NO\"}");
        updateField("{\"middleName\": \"test_middle_name\"}");
        updateField("{\"passportIssuedByCode\": \"123123\"}");
        updateField("{\"passportIssuedByName\": \"Отделение Тестового УВД\"}");
        updateField("{\"passportIssuedDate\": \"2020-01-01\"}");
        updateField("{\"passportNumber\": \"11112222\"}");
        updateField("{\"pension\": 10000}");
        updateField("{\"phoneNumber\": \"9999999999\"}");
        updateField("{\"prevFullName\": \"Тестовый Тестослав Тестович\"}");
        updateField("{\"proofOfIncome\": \"TWO_NDFL\"}");
        updateField("{\"registrationAddress\": \"test_address\"}");
        updateField("{\"registrationType\": \"PERMANENT\"}");
        updateField("{\"residenceAddress\": \"test_address_residence\"}");
        updateField("{\"residenceRF\": true}");
        updateField("{\"snils\": \"8877665544\"}");
        updateField("{\"totalWorkExperience\": \"FROM_1_TO_3\"}");
        updateField("{\"employer\": {\"address\": \"test_address\"}}");
        updateField("{\"employer\": {\"branch\": \"RESCUE\"}}");
        updateField("{\"employer\": {\"inn\": 555666677788}}");
        updateField("{\"employer\": {\"name\": \"test_name_employer\"}}");
        updateField("{\"employer\": {\"numberOfEmployees\": \"LESS_THAN_10\"}}");
        updateField("{\"employer\": {\"organizationAge\": \"LESS_THAN_1\"}}");
        updateField("{\"employer\": {\"phone\": \"4556667778\"}}");
        updateField("{\"employer\": {\"position\": \"director\"}}");
        updateField("{\"employer\": {\"site\": \"site.com\"}}");
        updateField("{\"employer\": {\"workExperience\": \"FROM_1_TO_3\"}}");
        updateField("{\"realEstate\": {\"address\": \"test_address\"}}");
        updateField("{\"realEstate\": {\"area\": 100}}");
        updateField("{\"realEstate\": {\"basisOfOwnership\": \"PURCHASE\"}}");
        updateField("{\"realEstate\": {\"isCollateral\": true}}");
        updateField("{\"realEstate\": {\"price\": 10000000}}");
        updateField("{\"realEstate\": {\"share\": 50}}");
        updateField("{\"realEstate\": {\"type\": \"APARTMENT\"}}");
        updateField("{\"vehicle\": {\"basisOfOwnership\": \"PURCHASE\"}}");
        updateField("{\"vehicle\": {\"isCollateral\": true}}");
        updateField("{\"vehicle\": {\"model\": \"LADA_BAKLAZHAN\"}}");
        updateField("{\"vehicle\": {\"price\": 1000000}}");
        updateField("{\"vehicle\": {\"yearOfManufacture\": 2000}}");

        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertEquals(10000, borrowerProfile.getAdditionalIncome());
        assertEquals(35, borrowerProfile.getAge());
        assertEquals(LocalDate.of(1990, 1, 1), borrowerProfile.getBirthdate());
        assertEquals(2, borrowerProfile.getChildren());
        assertEquals(Education.INCOMPLETE_SECONDARY, borrowerProfile.getEducation());
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
        assertEquals("test_address", borrowerProfile.getEmployer().getAddress());
        assertEquals(Branch.RESCUE, borrowerProfile.getEmployer().getBranch());
        assertEquals(555666677788L, borrowerProfile.getEmployer().getInn());
        assertEquals("test_name_employer", borrowerProfile.getEmployer().getName());
        assertEquals(NumberOfEmployees.LESS_THAN_10, borrowerProfile.getEmployer().getNumberOfEmployees());
        assertEquals(OrganizationAge.LESS_THAN_1, borrowerProfile.getEmployer().getOrganizationAge());
        assertEquals("4556667778", borrowerProfile.getEmployer().getPhone());
        assertEquals("director", borrowerProfile.getEmployer().getPosition());
        assertEquals("site.com", borrowerProfile.getEmployer().getSite());
        assertEquals(TotalWorkExperience.FROM_1_TO_3, borrowerProfile.getEmployer().getWorkExperience());
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
        updateField("{\"additionalIncome\": null}");
        updateField("{\"age\": null}");
        updateField("{\"birthdate\": null}");
        updateField("{\"children\": null}");
        updateField("{\"education\": null}");
        updateField("{\"email\": null}");
        updateField("{\"employmentStatus\": null}");
        updateField("{\"firstName\":null}");
        updateField("{\"gender\": null}");
        updateField("{\"lastName\": null}");
        updateField("{\"mainIncome\": null}");
        updateField("{\"maritalStatus\": null}");
        updateField("{\"marriageContract\": null}");
        updateField("{\"middleName\": null}");
        updateField("{\"passportIssuedByCode\": null}");
        updateField("{\"passportIssuedByName\": null}");
        updateField("{\"passportIssuedDate\": null}");
        updateField("{\"passportNumber\": null}");
        updateField("{\"pension\": null}");
        updateField("{\"phoneNumber\": null}");
        updateField("{\"prevFullName\": null}");
        updateField("{\"proofOfIncome\": null}");
        updateField("{\"registrationAddress\": null}");
        updateField("{\"registrationType\": null}");
        updateField("{\"residenceAddress\": null}");
        updateField("{\"residenceRF\": null}");
        updateField("{\"snils\": null}");
        updateField("{\"totalWorkExperience\": null}");
        updateField("{\"employer\": {\"address\": null}}");
        updateField("{\"employer\": {\"branch\": null}}");
        updateField("{\"employer\": {\"inn\": null}}");
        updateField("{\"employer\": {\"name\": null}}");
        updateField("{\"employer\": {\"numberOfEmployees\": null}}");
        updateField("{\"employer\": {\"organizationAge\": null}}");
        updateField("{\"employer\": {\"phone\": null}}");
        updateField("{\"employer\": {\"position\": null}}");
        updateField("{\"employer\": {\"site\": null}}");
        updateField("{\"employer\": {\"workExperience\": null}}");
        updateField("{\"realEstate\": {\"address\": null}}");
        updateField("{\"realEstate\": {\"area\": null}}");
        updateField("{\"realEstate\": {\"basisOfOwnership\": null}}");
        updateField("{\"realEstate\": {\"isCollateral\": null}}");
        updateField("{\"realEstate\": {\"price\": null}}");
        updateField("{\"realEstate\": {\"share\": null}}");
        updateField("{\"realEstate\": {\"type\": null}}");
        updateField("{\"vehicle\": {\"basisOfOwnership\": null}}");
        updateField("{\"vehicle\": {\"isCollateral\": null}}");
        updateField("{\"vehicle\": {\"model\": null}}");
        updateField("{\"vehicle\": {\"price\": null}}");
        updateField("{\"vehicle\": {\"yearOfManufacture\": null}}");

        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertNull(borrowerProfile.getAdditionalIncome());
        assertNull(borrowerProfile.getAge());
        assertNull(borrowerProfile.getBirthdate());
        assertNull(borrowerProfile.getChildren());
        assertNull(borrowerProfile.getEducation());
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
        assertNull(borrowerProfile.getEmployer().getAddress());
        assertNull(borrowerProfile.getEmployer().getBranch());
        assertNull(borrowerProfile.getEmployer().getInn());
        assertNull(borrowerProfile.getEmployer().getName());
        assertNull(borrowerProfile.getEmployer().getNumberOfEmployees());
        assertNull(borrowerProfile.getEmployer().getOrganizationAge());
        assertNull(borrowerProfile.getEmployer().getPhone());
        assertNull(borrowerProfile.getEmployer().getPosition());
        assertNull(borrowerProfile.getEmployer().getSite());
        assertNull(borrowerProfile.getEmployer().getWorkExperience());
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

    @Test
    public void testAddSalaryBanks() {
        updateField("{\"employer\": {\"salaryBanks\": [\"1fd0708a-d848-11ed-afa1-0242ac120002\", \"0c371042-d848-11ed-afa1-0242ac120002\"]}}");
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertNotNull(borrowerProfile.getEmployer().getSalaryBanks());
        Set<UUID> actualSalaryBanks = borrowerProfile.getEmployer().getSalaryBanks().stream()
                .map(Bank::getId)
                .collect(Collectors.toSet());
        Set<UUID> expectedBanks = new HashSet<>();
        expectedBanks.add(UUID.fromString("1fd0708a-d848-11ed-afa1-0242ac120002"));
        expectedBanks.add(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"));
        assertEquals(expectedBanks, actualSalaryBanks);
    }

    @Test
    public void testModifySalaryBanks() {
        updateField("{\"employer\": {\"salaryBanks\": [\"1fd0708a-d848-11ed-afa1-0242ac120002\", \"0c371042-d848-11ed-afa1-0242ac120002\"]}}");
        updateField("{\"employer\": {\"salaryBanks\": [\"2708daa4-d848-11ed-afa1-0242ac120002\", \"0c371042-d848-11ed-afa1-0242ac120002\"]}}");
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertNotNull(borrowerProfile.getEmployer().getSalaryBanks());
        Set<UUID> actualSalaryBanks = borrowerProfile.getEmployer().getSalaryBanks().stream()
                .map(Bank::getId)
                .collect(Collectors.toSet());
        assertEquals(2, actualSalaryBanks.size());
        Set<UUID> expectedBanks = new HashSet<>();
        expectedBanks.add(UUID.fromString("2708daa4-d848-11ed-afa1-0242ac120002"));
        expectedBanks.add(UUID.fromString("0c371042-d848-11ed-afa1-0242ac120002"));
        assertEquals(expectedBanks, actualSalaryBanks);
    }

    @Test
    public void testDeleteSalaryBanks() {
        updateField("{\"employer\": {\"salaryBanks\": [\"1fd0708a-d848-11ed-afa1-0242ac120002\", \"0c371042-d848-11ed-afa1-0242ac120002\"]}}");
        updateField("{\"employer\": {\"salaryBanks\": null}}");
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
        assertNotNull(borrowerProfile.getEmployer().getSalaryBanks());
        Set<UUID> actualSalaryBanks = borrowerProfile.getEmployer().getSalaryBanks().stream()
                .map(Bank::getId)
                .collect(Collectors.toSet());
        assertEquals(0, actualSalaryBanks.size());
    }

    private void updateField(String requestBody) {
        UUID borrowerProfileId = UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003");
        try {
            mockMvc.perform(put("/public/borrower_profile/" + borrowerProfileId + "/updateField")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}