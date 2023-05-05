package pro.mbroker.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import pro.mbroker.api.controller.BankController;
import pro.mbroker.app.util.AuthenticationTokenFilter;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import javax.servlet.Filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.yaml")
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BankControllerTest {
    @Autowired
    private BankController bankController;
    @MockBean
    private CurrentUserService currentUserService;
    @Autowired
    private TokenExtractor tokenExtractor;

    @Value("${test_token_with_admin_permission}")
    private String tokenWithAdminPermission;

    @Value("${test_token}")
    private String tokenWithoutAdminPermission;

    private MockMvc mockMvc;

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

        ResultActions resultActions = mockMvc.perform(post("/public/bank")
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
