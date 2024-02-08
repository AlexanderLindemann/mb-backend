package pro.mbroker.app.service;

import pro.mbroker.app.entity.BorrowerProfile;

import java.util.List;

public interface LinkService {

    void addLinksByProfiles(List<BorrowerProfile> borrowerProfiles, String prefixLink);
}
