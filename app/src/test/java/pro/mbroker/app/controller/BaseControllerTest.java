package pro.mbroker.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Sql(scripts = "classpath:sql/test_data.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.yaml")
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    protected CurrentUserService currentUserService;

    @Value("${test_token_with_admin_permission}")
    protected String tokenWithAdminPermission;
    @Value("${test_token}")
    protected String tokenWithoutAdminPermission;
    @Value("${test_token_with_organization_permission}")
    protected String tokenWithOrganizationPermission;
    @Value("${test_token_with_read_own_permission}")
    protected String tokenWithReadOwnPermission;

    public static final String PARTNER_APPLICATION_ID = "5ff4b32c-f967-4cb1-8705-7470a321fe34";
    public static final String PARTNER_APPLICATION_ID2 = "7addcbef-c1e0-4de1-adeb-377f864efcfa";

    @BeforeEach
    public void setUp() {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(tokenWithAdminPermission);
    }
}
