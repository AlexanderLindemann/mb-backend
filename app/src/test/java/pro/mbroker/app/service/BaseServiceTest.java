package pro.mbroker.app.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pro.smartdeal.common.security.Permission;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:sql/test_data.sql")
@TestPropertySource(locations = "classpath:application-test.yaml")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseServiceTest {

    @MockBean
    protected CurrentUserService currentUserService;
    @Value("${test_token}")
    protected String apiToken;
    @Value("${test_token_with_admin_permission}")
    protected String tokenWithAdminPermission;

    @Before
    public void setUp() {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(apiToken);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Permission.Code.MB_ADMIN_ACCESS));
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin", authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}

