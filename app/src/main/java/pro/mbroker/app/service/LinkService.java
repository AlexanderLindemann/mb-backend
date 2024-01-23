package pro.mbroker.app.service;

import pro.mbroker.app.entity.BorrowerProfile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface LinkService {

    void addLinksByProfiles(List<BorrowerProfile> borrowerProfiles, HttpServletRequest httpRequest);
}
