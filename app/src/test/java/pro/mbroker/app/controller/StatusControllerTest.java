package pro.mbroker.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.app.TestConstants;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.service.BorrowerProfileService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusControllerTest extends AbstractControllerTest {

    @Autowired
    private BorrowerProfileService borrowerProfileService;

    @Test
    public void testBorrowerProfileStatusChangeOnDataUpdateAfterSigning() {
        updateField("{\"additionalIncome\": 10000}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
    }


    @Test
    public void testBorrowerProfileStatusChangeOnProfileValue() {
        updateField("{\"mainIncome\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"mainIncome\": 100000}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"proofOfIncome\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"proofOfIncome\": \"TWO_NDFL\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportIssuedDate\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportIssuedDate\": \"2020-01-01\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportNumber\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportNumber\": \"11112222\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportIssuedByName\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportIssuedByName\": \"Отделение Тестового УВД\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportIssuedByCode\": null}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"passportIssuedByCode\": \"123123\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employmentStatus\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employmentStatus\": \"MILITARY\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"totalWorkExperience\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"totalWorkExperience\": \"FROM_1_TO_3\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"name\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"name\": \"test_name_employer\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"branch\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"branch\": \"RESCUE\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"tin\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"tin\": \"555666677788\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"phone\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"phone\": \"4556667778\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"numberOfEmployees\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"numberOfEmployees\": \"LESS_THAN_10\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"organizationAge\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"organizationAge\": \"LESS_THAN_1\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"address\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"address\": \"test_address\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"workExperience\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"workExperience\": \"FROM_1_TO_3\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"position\": null}}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"employer\": {\"position\": \"director\"}}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"firstName\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"firstName\": \"test_first_name\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"lastName\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"lastName\": \"test_last_name\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"phoneNumber\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"phoneNumber\": \"9999999999\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"birthdate\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"birthdate\": \"1990-01-01\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"gender\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"gender\": \"MALE\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"snils\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"snils\": \"8877665544\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"registrationAddress\": null}");
        assertEquals(BorrowerProfileStatus.DATA_NO_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());
        updateField("{\"registrationAddress\": \"test_address\"}");
        assertEquals(BorrowerProfileStatus.DATA_ENTERED, getBorrowerProfile(TestConstants.BORROWER_PROFILE_ID).getBorrowerProfileStatus());

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

    private BorrowerProfile getBorrowerProfile(UUID id) {
        return borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(id);
    }

}
