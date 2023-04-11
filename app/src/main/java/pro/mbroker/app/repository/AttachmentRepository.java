package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
