package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.BorrowerDocument;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BorrowerDocumentRepository extends JpaRepository<BorrowerDocument, UUID> {
    Optional<BorrowerDocument> findByAttachmentId(Long attachmentId);

    @Query(value = "SELECT bd.attachment.id " +
            "FROM BorrowerDocument bd " +
            "WHERE bd.isActive = true AND (" +
            "        (bd.bankApplication.id = :bankApplicationId AND bd.borrowerProfile.id IN :borrowerProfileIds) " +
            "        OR" +
            "        (bd.borrowerProfile.id IN :borrowerProfileIds) " +
            "    ) " +
            "  AND (bd.borrowerProfile.id, bd.updatedAt, bd.documentType) IN (" +
            "    SELECT bd2.borrowerProfile.id, MAX(bd2.updatedAt), bd2.documentType " +
            "    FROM BorrowerDocument bd2 " +
            "    WHERE bd2.isActive = true " +
            "      AND (bd2.bankApplication.id = :bankApplicationId " +
            "                AND bd2.borrowerProfile.id IN :borrowerProfileIds"  +
            "            OR" +
            "            (bd2.borrowerProfile.id IN :borrowerProfileIds)" +
            "        )\n" +
            "    GROUP BY bd2.borrowerProfile.id, bd2.documentType)     ")
    Set<Long> getAttachments(@Param("bankApplicationId") UUID bankApplicationId,
                             @Param("borrowerProfileIds") List<UUID> borrowerProfileIds);
}

