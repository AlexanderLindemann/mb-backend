package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.dto.request.CreateProfileRequest;
import pro.mbroker.app.model.profile.Profile;
import pro.mbroker.app.model.profile.ProfileRepository;
import pro.mbroker.app.service.ProfileService;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public void createProfile(CreateProfileRequest request) {
        Profile newProfile = new Profile();
        newProfile.setCreatedBy((long) currentUserService.getCurrentUserid());
        Profile profile = profileRepository.save(newProfile);
        log.debug("Create profile#{}", profile.getId());
    }
}
