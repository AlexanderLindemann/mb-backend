package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.enums.DocumentType;
import pro.mbroker.app.entity.BankApplication;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.PartnerApplication;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.*;
import pro.mbroker.app.util.Converter;
import pro.smartdeal.ng.attachment.api.AttachmentControllerService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final BorrowerDocumentService borrowerDocumentService;
    private final BorrowerDocumentMapper borrowerDocumentMapper;
    private final BankApplicationService bankApplicationService;
    private final BorrowerDocumentRepository borrowerDocumentRepository;

    @Override
    public Long upload(MultipartFile file) {
        return attachmentService.upload(file).getId();
    }

    @Override
    public BorrowerDocumentResponse uploadDocument(MultipartFile file,
                                                   UUID borrowerProfileId,
                                                   DocumentType documentType,
                                                   UUID bankId,
                                                   UUID bankApplicationId) {
        BorrowerProfile borrowerProfile = borrowerProfileService.getBorrowerProfile(borrowerProfileId);
        List<BankApplication> bankApplications = bankApplicationService.getBankApplicationByBorrowerId(borrowerProfileId);

        if (bankApplicationId != null) {
            bankApplications = bankApplications.stream()
                    .filter(bankApplication -> bankApplication.getId().equals(bankApplicationId))
                    .collect(Collectors.toList());
        }

        BorrowerDocumentRequest borrowerDocumentRequest = new BorrowerDocumentRequest()
                .setBorrowerProfileId(borrowerProfileId)
                .setDocumentType(documentType);
        if (Objects.nonNull(bankId)) {
            borrowerDocumentRequest.setBankId(bankId);
        }

        BorrowerDocument borrowerDocument = null;

        for (BankApplication bankApplication : bankApplications) {
            borrowerDocument = attachmentService.uploadDocument(file, borrowerDocumentRequest);
            borrowerDocument.setBankApplication(bankApplication);
            borrowerDocument.setBorrowerProfile(borrowerProfile);
            borrowerDocumentRepository.save(borrowerDocument);
        }


        partnerApplicationService.statusChanger(borrowerProfile.getPartnerApplication());
        Map<UUID, BorrowerProfile> borrowerProfileMap = borrowerProfile.getPartnerApplication().getBorrowerProfiles()
                .stream().collect(Collectors.toMap(BorrowerProfile::getId, Function.identity()));
        return borrowerDocument == null ? null : borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument)
                .setStatus(borrowerProfileMap.get(borrowerProfile.getId()).getBorrowerProfileStatus());
    }

    @Override  //TODO переделать логику. Убрать из этого метода borrowerDocumentService и сделать метод универсальным для аттачментов
    public void deleteDocument(Long attachmentId) {
        borrowerDocumentService.deleteDocumentByAttachmentId(attachmentId); //TODO Как только фронт переедет на deleteBorrowerDocument MB-285
        attachmentService.markAttachmentAsDeleted(attachmentId);
    }

    @Override
    public List<AttachmentInfo> getConvertedFiles(List<Long> attachmentsIds) {
        return attachmentService.getConvertedFiles(attachmentsIds);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(Long attachmentId) {
        return attachmentService.downloadFile(attachmentId);
    }

}
