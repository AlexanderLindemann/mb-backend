package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.service.UserService;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.common.security.Permission;

import java.util.EnumSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final static EnumSet<Permission> ALLOWED_PERMISSIONS = EnumSet.of(Permission.MB_ADMIN_ACCESS,
                                                                              Permission.MB_REQUEST_READ_OWN,
                                                                              Permission.MB_REQUEST_READ_ORGANIZATION);

    private final TokenExtractor tokenExtractor;

    @Override
    public UserResponse getUserInformation(String token) {

        EnumSet<Permission> permissions = tokenExtractor.getPermissions(token).stream()
                .map(s -> {
                    try {
                        return Permission.valueOf(s);
                    } catch (IllegalArgumentException e) {
                        log.error("An unknown permission value was encountered: " + s, e);
                        return null;
                    }
                })
                .filter(permission -> permission != null && ALLOWED_PERMISSIONS.contains(permission))
                .collect(() -> EnumSet.noneOf(Permission.class), EnumSet::add, EnumSet::addAll);


        return new UserResponse()
                .setPermissions(permissions);

    }
}
