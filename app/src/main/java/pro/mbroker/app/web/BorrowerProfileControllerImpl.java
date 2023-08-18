package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BorrowerProfileController;
import pro.mbroker.api.dto.request.BorrowerProfileUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileFullResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowerProfileControllerImpl implements BorrowerProfileController {

    private final BorrowerProfileService borrowerProfileService;
    private final BorrowerDocumentService borrowerDocumentService;
    private final BorrowerProfileMapper borrowerProfileMapper;


    @Override
    public BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request) {
        return borrowerProfileService.createOrUpdateBorrowerProfile(request);
    }

    @Override
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request) {
        return borrowerProfileService.createOrUpdateGenericBorrowerProfile(request);
    }

    @Override
    public BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId) {
        return borrowerProfileService.getBorrowersByPartnerApplicationId(partnerApplicationId);
    }

    @Override
    public void deleteBorrowerProfileById(UUID borrowerProfileId) {
        borrowerProfileService.deleteBorrowerProfileById(borrowerProfileId);
    }

    @Override
    public void deleteBorrowerDocument(Long attachmentId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId);
    }

    @Override
    public void updateBorrowerProfileField(UUID borrowerProfileId, BorrowerProfileUpdateRequest updateRequest) {
        borrowerProfileService.updateBorrowerProfileField(borrowerProfileId, updateRequest);
    }

    @Override
    public BorrowerProfileFullResponse getBorrower(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfileWithEmployer(borrowerProfileId);
        return borrowerProfileMapper.toBorrowerProfileFullResponse(borrowerProfile);
    }
}
