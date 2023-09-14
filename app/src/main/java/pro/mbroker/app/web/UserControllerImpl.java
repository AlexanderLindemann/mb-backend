package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.UserController;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.service.UserService;

@RequiredArgsConstructor
@Component
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public UserResponse getUserInformation(String token) {
        return userService.getUserInformation(token);
    }
}
