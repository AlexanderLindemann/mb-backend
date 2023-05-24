package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerApplicationController;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.service.PartnerApplicationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApplicationControllerImpl implements PartnerApplicationController {
    private final PartnerApplicationService partnerApplicationService;
    private final PartnerApplicationMapper partnerApplicationMapper;

    @Override
    public List<PartnerApplicationResponse> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder) {
        return partnerApplicationService.getAllPartnerApplication(page, size, sortBy, sortOrder)
                .stream()
                .map(partnerApplicationService::getCalculateMortgage)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or" +
            " hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or" +
            " hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public PartnerApplicationResponse getPartnerApplicationById(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplicationByIdWithPermission(partnerApplicationId);
        return partnerApplicationService.getCalculateMortgage(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(request);
        return partnerApplicationService.getCalculateMortgage(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.updatePartnerApplication(partnerApplicationId, request);
        return partnerApplicationService.getCalculateMortgage(partnerApplication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deletePartnerApplication(UUID partnerApplicationId) {
        partnerApplicationService.deletePartnerApplication(partnerApplicationId);
    }

}
