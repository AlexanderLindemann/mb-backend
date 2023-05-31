package pro.mbroker.app.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.AttachmentController;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.mapper.BorrowerDocumentMapper;
import pro.mbroker.app.service.AttachmentService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final AttachmentService attachmentService;
    private final BorrowerDocumentMapper borrowerDocumentMapper;

    @Override
    public Long upload(MultipartFile file) {
        return attachmentService.upload(file).getId();
    }

    @Override
    public BorrowerDocumentResponse uploadDocument(MultipartFile file,
                                                   BorrowerDocumentRequest documentDto) {
        BorrowerDocument borrowerDocument = attachmentService.uploadDocument(file, documentDto);
        return borrowerDocumentMapper.toBorrowerDocumentResponse(borrowerDocument);
    }

    @Override
    public MultipartFile download(Long attachmentId) {
        return attachmentService.download(attachmentId);
    }

}
