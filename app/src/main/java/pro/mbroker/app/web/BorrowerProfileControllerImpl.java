package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.BorrowerProfileController;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.response.BorrowerProfileForUpdateResponse;
import pro.mbroker.api.dto.response.BorrowerResponse;
import pro.mbroker.api.enums.Education;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerProfileMapper;
import pro.mbroker.app.mapper.PartnerApplicationMapper;
import pro.mbroker.app.service.BorrowerDocumentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.util.Converter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowerProfileControllerImpl implements BorrowerProfileController {

    private final BorrowerProfileService borrowerProfileService;
    private final BorrowerDocumentService borrowerDocumentService;
    private final BorrowerProfileMapper borrowerProfileMapper;
    private final PartnerApplicationMapper partnerApplicationMapper;

    @Override
    public BorrowerResponse createOrUpdateBorrowerProfile(BorrowerRequest request, Integer sdId) {
        return borrowerProfileService.createOrUpdateBorrowerProfile(request, sdId);
    }

    @Override
    public BorrowerResponse createOrUpdateGenericBorrowerProfile(BorrowerRequest request, HttpServletRequest httpRequest, Integer sdId) {
        return borrowerProfileService.createOrUpdateGenericBorrowerProfile(request, httpRequest, sdId);
    }

    @Override
    public BorrowerResponse getBorrowersByPartnerApplicationId(UUID partnerApplicationId) {
        return borrowerProfileService.getBorrowersByPartnerApplicationId(partnerApplicationId);
    }

    @Override
    public void deleteBorrowerProfileById(UUID borrowerProfileId, Integer sdId) {
        borrowerProfileService.deleteBorrowerProfileById(borrowerProfileId, sdId);
    }

    @Override
    public void deleteBorrowerDocument(Long attachmentId, Integer sdId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId, sdId);
    }

    @Override
    public void updateBorrowerProfileField(UUID borrowerProfileId, Map<String, Object> fieldsMap) {
        borrowerProfileService.updateBorrowerProfileField(borrowerProfileId, fieldsMap);
    }

    @Override
    public BorrowerProfileForUpdateResponse getFullBorrower(UUID borrowerProfileId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getFullBorrower(borrowerProfileId);
        BorrowerProfileForUpdateResponse borrowerProfileForUpdate = borrowerProfileMapper.toBorrowerProfileForUpdate(borrowerProfile);
        if (borrowerProfile.getEducations() != null) {
            borrowerProfileForUpdate.setEducations(Converter.convertStringListToEnumList(borrowerProfile.getEducations(), Education.class));
        }
        borrowerProfileForUpdate.setPartnerApplication(partnerApplicationMapper.
                toPartnerApplicationResponse(borrowerProfile.getPartnerApplication()));
        return borrowerProfileForUpdate;
    }
}
