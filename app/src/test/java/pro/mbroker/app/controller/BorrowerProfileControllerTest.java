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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BorrowerProfileControllerTest extends AbstractControllerTest {

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
}