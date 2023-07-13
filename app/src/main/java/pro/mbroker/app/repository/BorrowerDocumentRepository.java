package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.BorrowerDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowerDocumentRepository extends JpaRepository<BorrowerDocument, UUID> {
    Optional<BorrowerDocument> findByAttachmentId(Long attachmentId);

    @Query(value = "SELECT a.id " +
            "FROM BorrowerDocument bd " +
            "JOIN bd.attachment a " +
            "JOIN bd.borrowerProfile bp " +
            "JOIN bp.partnerApplication pa " +
            "WHERE pa.id = :partnerApplicationId AND a.isActive = true")
    List<Long> getaAttachmentIds(@Param("partnerApplicationId") UUID partnerApplicationId);
}

