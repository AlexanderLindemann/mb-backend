package pro.mbroker.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileResponse;
import pro.mbroker.app.TestData;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.service.BorrowerProfileService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BorrowerProfileControllerTest extends BaseControllerTest {
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
        assertEquals("Ivanov Perviy", JsonPath.read(responseBody, "$.mainBorrower.lastName"));
        assertEquals("Ivanovich", JsonPath.read(responseBody, "$.mainBorrower.middleName"));
        assertEquals("9876543219", JsonPath.read(responseBody, "$.mainBorrower.phoneNumber"));
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