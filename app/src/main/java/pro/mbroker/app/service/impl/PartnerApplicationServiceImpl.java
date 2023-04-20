package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.PartnerApplicationRepository;
import pro.mbroker.app.repository.PartnerRepository;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.mbroker.app.service.PartnerService;
import pro.mbroker.app.util.TokenExtractor;
import pro.smartdeal.ng.common.security.service.CurrentUserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationServiceImpl implements PartnerApplicationService {
    private final PartnerService partnerService;
    private final PartnerRepository partnerRepository;
    private final CurrentUserService currentUserService;
    private final PartnerApplicationRepository partnerApplicationRepository;

    @Override
    public List<PartnerApplication> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        log.info("Getting all partner applications with pagination: page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);

        Pageable pageable = partnerService.createPageable(page, size, sortBy, sortOrder);
        Partner partner = getPartnerByOrganizationId();

        List<PartnerApplication> partnerApplicationPage = partnerApplicationRepository.findAllByPartner(partner, pageable);
        log.info("Found {} partner applications for organization ID: {}", partnerApplicationPage.size(), partner.getSmartDealOrganizationId());

        return partnerApplicationPage;
    }

    private Partner getPartnerByOrganizationId() {
        String currentUserToken = currentUserService.getCurrentUserToken();
        int organizationId = TokenExtractor.extractSdCurrentOrganizationId(currentUserToken);

        log.info("Retrieving partner by organization ID: {}", organizationId);
        return partnerRepository.findBySmartDealOrganizationId(organizationId)
                .orElseThrow(() -> new ItemNotFoundException(Partner.class, String.valueOf(organizationId)));
    }
}
