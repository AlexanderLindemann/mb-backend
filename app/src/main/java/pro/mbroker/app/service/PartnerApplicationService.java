package pro.mbroker.app.service;

import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;

public interface PartnerApplicationService {
    List<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder);
}
