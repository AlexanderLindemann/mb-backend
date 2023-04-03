package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.controller.ClientProfileController;
import pro.mbroker.api.dto.ProfilesList;
import pro.mbroker.api.dto.request.CreateProfileRequest;
import pro.mbroker.app.mapper.ProfileMapper;
import pro.mbroker.app.model.profile.Profile;
import pro.mbroker.app.model.profile.ProfileRepository;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ClientProfileControllerImpl implements ClientProfileController {

    private final ProfileRepository profileRepository;
    private final ProfileMapper mapper;
    private final CurrentUserService currentUserService;

    @Override
    public ProfilesList listProfiles() {
        return null;
    }

    @Override
    public void createProfile(CreateProfileRequest request) {
        Profile newProfile = new Profile();
        newProfile.setCreatedBy((long) currentUserService.getCurrentUserid());
        Profile profile = profileRepository.save(newProfile);
        log.debug("Create profile#{}", profile.getId());
    }

}
