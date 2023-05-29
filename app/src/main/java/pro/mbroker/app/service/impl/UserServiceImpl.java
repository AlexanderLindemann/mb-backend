package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.exception.UnknownPermissionValueException;
import pro.mbroker.app.service.UserService;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.common.security.Permission;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final static EnumSet<Permission> ALLOWED_PERMISSIONS = EnumSet.of(Permission.MB_ADMIN_ACCESS,
                                                                              Permission.MB_ACCESS,
                                                                              Permission.MB_REQUEST_READ_OWN,
                                                                              Permission.MB_REQUEST_READ_ORGANIZATION);

    private final TokenExtractor tokenExtractor;

    @Override
    public UserResponse getUserInformation(String token) {

        EnumSet<Permission> permissions;
        try {
            permissions = tokenExtractor.getPermissions(token).stream()
                    .map(Permission::valueOf)
                    .filter(ALLOWED_PERMISSIONS::contains)
                    .collect(() -> EnumSet.noneOf(Permission.class), EnumSet::add, EnumSet::addAll);
        } catch (IllegalArgumentException e) {
            throw new UnknownPermissionValueException("An unknown permission value was encountered.", e);
        }

        return new UserResponse()
                .setPermissions(permissions);

    }
}
