package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.CreateProfileRequest;

public interface ProfileService {
    void createProfile(CreateProfileRequest request);
}
