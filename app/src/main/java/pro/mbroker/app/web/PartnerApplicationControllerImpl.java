package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.PartnerApplicationController;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.dto.request.PartnerFilter;
import pro.mbroker.api.dto.response.PartnerApplicationResponse;
import pro.mbroker.api.dto.response.RequiredDocumentResponse;
import pro.mbroker.api.enums.BankApplicationStatus;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.service.PartnerApplicationService;
import pro.smartdeal.common.security.Permission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApplicationControllerImpl implements PartnerApplicationController {
    private final PartnerApplicationService partnerApplicationService;

    @Override
    public Page<PartnerApplicationResponse> getAllPartnerApplication(int page,
                                                                     int size,
                                                                     String sortBy,
                                                                     String sortOrder,
                                                                     LocalDateTime startDate,
                                                                     LocalDateTime endDate,
                                                                     Permission permission) {
        Page<PartnerApplication> partnerApplications =
                partnerApplicationService.getAllPartnerApplication(page,
                        size,
                        sortBy,
                        sortOrder,
                        startDate,
                        endDate,
                        permission);
        Page<PartnerApplicationResponse> responsePage =
                partnerApplications.map(partnerApplicationService::buildPartnerApplicationResponse);
        return responsePage;
    }

    @Override
    public PartnerApplicationResponse getPartnerApplicationById(UUID partnerApplicationId, Permission permission) {
        PartnerApplication partnerApplication =
                partnerApplicationService.getPartnerApplicationByIdWithPermission(partnerApplicationId, permission);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse createPartnerApplication(PartnerApplicationRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.createPartnerApplication(request);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse updatePartnerApplication(UUID partnerApplicationId,
                                                               PartnerApplicationRequest request,
                                                               Permission permission) {
        PartnerApplication partnerApplication = partnerApplicationService.updatePartnerApplication(
                partnerApplicationId,
                request,
                permission);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public void deletePartnerApplication(UUID partnerApplicationId) {
        partnerApplicationService.deletePartnerApplication(partnerApplicationId);
    }

    @Override
    public List<PartnerApplicationResponse> filter(PartnerFilter filter,  String sortBy, String sortDirection) {
        return partnerApplicationService.search(filter, sortBy, sortDirection);
    }

    @Override
    public PartnerApplicationResponse enableBankApplication(UUID partnerApplicationId, BankApplicationUpdateRequest request) {
        PartnerApplication partnerApplication = partnerApplicationService.enableBankApplication(partnerApplicationId, request);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public PartnerApplicationResponse disableBankApplication(UUID partnerApplicationId, UUID creditProgramId) {
        PartnerApplication partnerApplication = partnerApplicationService.disableBankApplication(
                partnerApplicationId,
                creditProgramId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }

    @Override
    public List<RequiredDocumentResponse> getRequiredDocuments(UUID partnerApplicationId) {
        return partnerApplicationService.getRequiredDocuments(partnerApplicationId);
    }

    @Override
    public PartnerApplicationResponse changeMainBorrowerByPartnerApplication(UUID partnerApplicationId,
                                                                             UUID newMainBorrowerId) {
        PartnerApplication partnerApplication =
                partnerApplicationService.changeMainBorrowerByPartnerApplication(partnerApplicationId, newMainBorrowerId);
        return partnerApplicationService.buildPartnerApplicationResponse(partnerApplication);
    }
}
