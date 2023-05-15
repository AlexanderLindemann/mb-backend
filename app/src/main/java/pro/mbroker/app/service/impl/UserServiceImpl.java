package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.service.UserService;
import pro.mbroker.app.util.TokenExtractor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TokenExtractor tokenExtractor;

    @Override
    public UserResponse getUserInformation(String token) {
        return new UserResponse()
                .setPermissions(tokenExtractor.getPermissions(token));

    }
}
