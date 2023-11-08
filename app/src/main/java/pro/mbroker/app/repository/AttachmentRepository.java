package pro.mbroker.app.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pro.mbroker.app.entity.Attachment;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findAttachmentById(Long externalId);

    @Modifying
    @Query("UPDATE Attachment a SET a.isActive = false WHERE a.id IN :attachmentIds")
    void setAttachmentsInactive(@Param("ids") List<Long> attachmentIds);
}
