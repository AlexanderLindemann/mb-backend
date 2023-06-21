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
            "WHERE bd.borrowerProfile.id = :bankApplicationId")
    List<Long> getaAttachmentIds(@Param("bankApplicationId") UUID bankApplicationId);
}
