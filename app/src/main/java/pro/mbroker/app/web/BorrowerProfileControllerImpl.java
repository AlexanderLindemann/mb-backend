package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BorrowerProfileController;
import pro.mbroker.api.dto.request.BorrowerProfileUpdateRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileForUpdateResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.FormService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowerProfileControllerImpl implements BorrowerProfileController {

    private final BorrowerProfileService borrowerProfileService;
    private final BorrowerDocumentService borrowerDocumentService;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final FormService formService;


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
    public BorrowerProfileForUpdateResponse getBorrower(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.findByIdWithRealEstateVehicleAndEmployer(borrowerProfileId);
        return borrowerProfileMapper.toBorrowerProfileForUpdate(borrowerProfile);
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateFormFile(UUID borrowerProfileId) {
        return formService.generateFormFile(borrowerProfileId);
    }

    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFile(UUID borrowerProfileId, byte[] signature) {
        return formService.signatureFormFile(borrowerProfileId, signature);
    }

    @Override
    public void updateGeneratedForm(UUID borrowerProfileId, byte[] form) {
        formService.updateGeneratedForm(borrowerProfileId, form);
    }

    @Override
    public void updateSignatureForm(UUID borrowerProfileId, byte[] form) {
        formService.updateSignatureForm(borrowerProfileId, form);
    }
}
