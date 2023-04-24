package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerApplicationController;
import pro.mbroker.api.dto.PartnerApplicationDto;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.smartdeal.common.security.Permission;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApplicationControllerImpl implements PartnerApplicationController {
    private final PartnerApplicationService partnerApplicationService;
    private final PartnerApplicationMapper partnerApplicationMapper;

    @Override
    @Secured(Permission.Code.MB_REQUEST_READ_ORGANIZATION)
    public List<PartnerApplicationDto> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        List<PartnerApplication> allPartnerApplication = partnerApplicationService.getAllPartnerApplication(page, size, sortBy, sortOrder);
        return partnerApplicationMapper.toDtoList(allPartnerApplication);
    }
}
