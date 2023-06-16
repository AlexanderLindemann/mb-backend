package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.BorrowerDocument;

import java.util.Optional;
import java.util.UUID;

public interface BorrowerDocumentRepository extends JpaRepository<BorrowerDocument, UUID> {
    Optional<BorrowerDocument> findByAttachmentId(Long attachmentId);
}
