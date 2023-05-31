package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.response.UserResponse;


@Api(value = "API Получения данных пользователя", tags = "API Получения данных пользователя")
@RestController
@RequestMapping("/public/v1/user")
public interface UserController { // Контроллер, который отдает информацию о пользователе

    @ApiOperation("Получение прав пользователя")
    @GetMapping("information")
    UserResponse getUserInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}
