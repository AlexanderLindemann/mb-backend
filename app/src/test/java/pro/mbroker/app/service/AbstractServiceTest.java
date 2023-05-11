package pro.mbroker.app.service;

import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

@TestPropertySource(locations = "classpath:application-test.yaml")
public abstract class AbstractServiceTest {

    @MockBean
    protected CurrentUserService currentUserService;

    @Value("${test_token}")
    protected String apiToken;

    @Before
    public void setUp() {
        Mockito.when(currentUserService.getCurrentUserToken()).thenReturn(apiToken);
    }
}

