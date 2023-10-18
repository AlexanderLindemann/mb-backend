package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerApplicationController;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.PartnerApplicationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApplicationControllerImpl implements PartnerApplicationController {
    private final PartnerApplicationService partnerApplicationService;

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or" +
            " hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or" +
            " hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public Page<PartnerApplicationResponse> getAllPartnerApplication(int page, int size, String sortBy, String sortOrder, LocalDateTime startDate, LocalDateTime endDate) {
        Page<PartnerApplication> partnerApplications = partnerApplicationService.getAllPartnerApplication(page, size, sortBy, sortOrder, startDate, endDate);
        return partnerApplications.map(partnerApplicationService::buildPartnerApplicationResponse);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or" +
            " hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or" +
            " hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public PartnerApplicationResponse getPartnerApplicationById(UUID partnerApplicationId) {
        PartnerApplication partnerApplication = partnerApplicationService.getPartnerApplicationByIdWithPermission(partnerApplicationId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or" +
            " hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or" +
            " hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public PartnerApplicationResponse createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(request);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS) or" +
            " hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_OWN) or" +
            " hasAnyAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_REQUEST_READ_ORGANIZATION)")
    public PartnerApplicationResponse updatePartnerApplication(UUID partnerApplicationId, PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.updatePartnerApplication(partnerApplicationId, request);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public void deletePartnerApplication(UUID partnerApplicationId) {
        partnerApplicationService.deletePartnerApplication(partnerApplicationId);
    }

    @Override
    public List<PartnerApplicationResponse> filter(String firstName,
                                                   String middleName,
                                                   String lastName,
                                                   String phoneNumber,
                                                   String residentialComplexName,
                                                   RegionType region,
                                                   String bankName,
                                                   BankApplicationStatus applicationStatus,
                                                   String sortBy,
                                                   String sortDirection) {
        return partnerApplicationService.search(firstName,
                middleName,
                lastName,
                phoneNumber,
                residentialComplexName,
                region,
                bankName,
                applicationStatus,
                sortBy,
                sortDirection);
    }

    @Override
    public PartnerApplicationResponse enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.enableBankApplication(partnerApplicationId, request);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse disableBankApplication(UUID partnerApplicationId, UUID creditProgramId) {
        PartnerApplication partnerApplication = partnerApplicationService.disableBankApplication(partnerApplicationId, creditProgramId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId) {
        return partnerApplicationService.getRequiredDocuments(partnerApplicationId);
    }

    @Override
    @PreAuthorize("hasAuthority(T(pro.smartdeal.common.security.Permission$Code).MB_ADMIN_ACCESS)")
    public PartnerApplicationResponse changeMainBorrowerByPartnerApplication(UUID partnerApplicationId, UUID newMainBorrowerId) {
        PartnerApplication partnerApplication = partnerApplicationService.changeMainBorrowerByPartnerApplication(partnerApplicationId, newMainBorrowerId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

}
