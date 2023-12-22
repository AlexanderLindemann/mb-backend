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
import pro.mbroker.api.enums.Education;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.FormService;
import pro.mbroker.app.util.Converter;

import javax.servlet.http.HttpServletRequest;
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
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request, HttpServletRequest httpRequest) {
        return borrowerProfileService.createOrUpdateGenericBorrowerProfile(request, httpRequest);
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
    public void updateBorrowerProfileField(UUID borrowerProfileId, BorrowerProfileUpdateRequest updateRequest, HttpServletRequest httpRequest) {
        borrowerProfileService.updateBorrowerProfileField(borrowerProfileId, updateRequest, httpRequest);
    }

    @Override
    public BorrowerProfileForUpdateResponse getBorrower(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getFullBorrower(borrowerProfileId);
        BorrowerProfileForUpdateResponse borrowerProfileForUpdate = borrowerProfileMapper.toBorrowerProfileForUpdate(borrowerProfile);
        if (borrowerProfile.getEducations() != null) {
            borrowerProfileForUpdate.setEducations(Converter.convertStringListToEnumList(borrowerProfile.getEducations(), Education.class));
        }
        return borrowerProfileForUpdate;
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateFormFileDocx(UUID borrowerProfileId) {
        return formService.generateFormFileDocx(borrowerProfileId);
    }

    //todo удалить после тестов
    @Override
    public ResponseEntity<ByteArrayResource> generateFormFileTest(UUID borrowerProfileId, byte[] file) {
        return formService.generateFormFileTest(borrowerProfileId, file);
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
