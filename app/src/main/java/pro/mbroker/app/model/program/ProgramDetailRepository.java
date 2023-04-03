package pro.mbroker.app.model.program;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgramDetailRepository extends JpaRepository<ProgramDetail, UUID> {
}
