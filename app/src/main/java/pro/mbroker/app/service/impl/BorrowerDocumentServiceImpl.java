package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.BorrowerDocumentRepository;
import pro.mbroker.app.service.BorrowerDocumentService;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowerDocumentServiceImpl implements BorrowerDocumentService {
    private final BorrowerDocumentRepository borrowerDocumentRepository;

    @Override
    public void deleteDocumentByAttachmentId(Long attachmentId) {
        BorrowerDocument borrowerDocument = getBorrowerDocumentByAttachmentId(attachmentId);
        borrowerDocument.setActive(false);
        borrowerDocumentRepository.save(borrowerDocument);
    }

    @Override
    public BorrowerDocument getBorrowerDocumentByAttachmentId(Long attachmentId) {
        return borrowerDocumentRepository.findByAttachmentId(attachmentId)
                .orElseThrow(() -> new ItemNotFoundException(BorrowerDocument.class, attachmentId));
    }
}
