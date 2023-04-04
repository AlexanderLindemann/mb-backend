package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.ClientProfileController;
import pro.mbroker.api.dto.ProfilesList;
import pro.mbroker.api.dto.request.CreateProfileRequest;
import pro.mbroker.app.service.ProfileService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientProfileControllerImpl implements ClientProfileController {

    private final ProfileService profileService;

    @Override
    public ProfilesList listProfiles() {
        return null;
    }

    @Override
    public void createProfile(CreateProfileRequest request) {
        profileService.createProfile(request);
    }

}
