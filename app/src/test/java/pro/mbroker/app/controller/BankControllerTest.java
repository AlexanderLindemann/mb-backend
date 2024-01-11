package pro.mbroker.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;
import pro.mbroker.app.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankControllerTest extends AbstractControllerTest {
    @Autowired
    private TestData testData;

    @Test
    public void createBank_withValidToken() throws Exception {
        ResultActions authorization = mockMvc.perform(post("/public/bank")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenWithAdminPermission)
                .content(new ObjectMapper().writeValueAsString(testData.getBankRequest())));
        authorization
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Bank"))
                .andExpect(jsonPath("$.contacts[0].fullName").value("test full name 1"))
                .andExpect(jsonPath("$.contacts[0].email").value("test email 1"))
                .andExpect(jsonPath("$.contacts[1].fullName").value("test full name 2"))
                .andExpect(jsonPath("$.contacts[1].email").value("test email 2"));
    }

    @Test
    public void createBank_withNotValidToken() throws Exception {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(tokenWithoutAdminPermission);
        try {
            mockMvc.perform(post("/public/bank")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + tokenWithoutAdminPermission)
                            .content(new ObjectMapper().writeValueAsString(testData.getBankRequest())))
                    .andExpect(status().isForbidden());
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof AccessDeniedException, "Expected AccessDeniedException");
            assertEquals("Access is denied", e.getCause().getMessage());
        }
    }
}
