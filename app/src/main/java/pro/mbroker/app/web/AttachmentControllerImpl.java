package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
import pro.mbroker.app.service.AttachmentService;
import pro.mbroker.app.service.BorrowerProfileService;
import pro.mbroker.app.service.PartnerApplicationService;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final AttachmentService attachmentService;
    private final BorrowerProfileService borrowerProfileService;
    private final PartnerApplicationService partnerApplicationService;
    private final BorrowerDocumentMapper borrowerDocumentMapper;

    @Override
    public Long upload(MultipartFile file) {
        return attachmentService.upload(file).getId();
    }

    @Override
    public BorrowerDocumentResponse uploadDocument(MultipartFile file,
                                                   BorrowerDocumentRequest documentDto) {
        BorrowerDocument borrowerDocument = attachmentService.uploadDocument(file, documentDto);
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(documentDto.getBorrowerProfileId());
        partnerApplicationService.statusChanger(borrowerProfile.getPartnerApplication());
        Map<UUID, BorrowerProfile> borrowerProfileMap = borrowerProfile.getPartnerApplication().getBorrowerProfiles()
                .stream().collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
        return borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument)
                .setStatus(borrowerProfileMap.get(borrowerProfile.getId()).getBorrowerProfileStatus());
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        return attachmentService.download(attachmentId);
    }

}
