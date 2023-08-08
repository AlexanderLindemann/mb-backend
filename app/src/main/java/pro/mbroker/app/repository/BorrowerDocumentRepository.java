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

    @Query(value = "SELECT bd.attachment.id " +
            "FROM BorrowerDocument bd " +
            "WHERE bd.bankApplication.id = :bankApplicationId AND bd.isActive = true")
    List<Long> getAttachmentIds(@Param("bankApplicationId") UUID bankApplicationId);

    @Query(value = "SELECT bd.attachment.id " +
            "FROM BorrowerDocument bd " +
            "WHERE bd.borrowerProfile.id = :borrowerProfileId AND bd.bank.id IS NULL AND bd.isActive = true")
    List<Long> getAttachmentsWithoutBankId(@Param("borrowerProfileId") UUID borrowerProfileId);

}

