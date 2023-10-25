package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.controller.AbstractControllerTest;
import pro.mbroker.app.service.impl.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@Disabled
public class UserServiceTest extends AbstractControllerTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    private static final List<String> PERMISSION_SCOPES =
            List.of("MB_REQUEST_READ_OWN",
                    "MB_REQUEST_READ_ORGANIZATION",
                    "MB_ADMIN_ACCESS");

    @Test
    public void testGetUserInformation() {
        UserResponse userInformation = userServiceImpl.getUserInformation(tokenWithAdminPermission);
        Assert.assertEquals(PERMISSION_SCOPES, userInformation.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

}
