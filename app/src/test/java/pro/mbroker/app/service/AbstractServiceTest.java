package pro.mbroker.app.service;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import pro.smartdeal.common.security.Permission;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.ArrayList;
import java.util.List;

@Ignore
@TestPropertySource(locations = "classpath:application-test.yaml")
public abstract class AbstractServiceTest {

    @MockBean
    protected CurrentUserService currentUserService;

    @Value("${test_token}")
    protected String apiToken;

    @Before
    public void setUp() {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(apiToken);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Permission.Code.MB_ADMIN_ACCESS));
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin", authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}

