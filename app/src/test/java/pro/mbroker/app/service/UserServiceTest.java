package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.service.impl.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceTest extends BaseServiceTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    private static final List<String> PERMISSION_SCOPES =
            List.of("MB_REQUEST_READ_ORGANIZATION",
                    "MB_ADMIN_ACCESS");

    @Test
    public void testGetUserInformation() {
        UserResponse userInformation = userServiceImpl.getUserInformation(tokenWithAdminPermission);
        Assert.assertEquals(PERMISSION_SCOPES, userInformation.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

}
