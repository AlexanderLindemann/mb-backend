package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "(bd.bankApplication.id = :bankApplicationId AND bd.borrowerProfile.id IN :borrowerProfileIds) OR " +
            "(bd.borrowerProfile.id IN :borrowerProfileIds)) ")
    Set<Long> getAttachments(@Param("bankApplicationId") UUID bankApplicationId,
                             @Param("borrowerProfileIds") List<UUID> borrowerProfileIds);

    @Modifying
    @Query("UPDATE BorrowerDocument bd SET bd.isActive = false WHERE bd.attachment.id IN :attachmentIds")
    void setAttachmentsInactive(@Param("attachmentIds") List<Long> attachmentIds);

}

