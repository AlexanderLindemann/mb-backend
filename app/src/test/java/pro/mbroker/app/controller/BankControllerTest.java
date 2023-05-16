package pro.mbroker.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import pro.mbroker.api.controller.BankController;
import pro.mbroker.app.util.AuthenticationTokenFilter;
import pro.mbroker.app.util.TokenExtractor;

import javax.servlet.Filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BankControllerTest extends AbstractControllerTest {
    @Autowired
    private BankController bankController;
    @Autowired
    private TokenExtractor tokenExtractor;

    @BeforeEach
    public void setUp() {
        Filter authenticationTokenFilter = new AuthenticationTokenFilter(tokenExtractor);

        mockMvc = MockMvcBuilders.standaloneSetup(bankController)
                .addFilter(authenticationTokenFilter)
                .build();

        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(tokenWithAdminPermission);
    }

    @Test
    public void createBank_withValidToken() throws Exception {
        String bankName = "Test Bank";

        mockMvc.perform(post("/public/bank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenWithAdminPermission)
                        .param("name", bankName))
                .andExpect(status().isOk());
    }

    @Test
    public void createBank_withNotValidToken() throws Exception {
        String bankName = "Test Bank2";

        try {
            mockMvc.perform(post("/public/bank")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + tokenWithoutAdminPermission)
                            .param("name", bankName))
                    .andExpect(status().isForbidden());
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof AccessDeniedException, "Expected AccessDeniedException");
            assertEquals("Access is denied", e.getCause().getMessage());
        }
    }
}
