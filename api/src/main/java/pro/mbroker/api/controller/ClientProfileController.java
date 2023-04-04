package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.CreateProfileRequest;
import pro.mbroker.api.dto.ProfilesList;

@Api("Создание API")
@RestController
@RequestMapping("/public/profile")
public interface ClientProfileController {

    @ApiOperation("Получить список профилей")
    @GetMapping
    ProfilesList listProfiles();

    @ApiOperation("Создать профиль")
    @PostMapping
    void createProfile(@RequestBody CreateProfileRequest request);

}
