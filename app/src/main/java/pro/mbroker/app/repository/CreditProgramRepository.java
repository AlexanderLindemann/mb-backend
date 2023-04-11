package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.CreditProgram;

import java.util.List;
import java.util.UUID;

public interface CreditProgramRepository extends JpaRepository<CreditProgram, UUID> {
    List<CreditProgram> findAllByBankId(UUID bankId);

    List<CreditProgram> findAllByIdIn(List<UUID> bankIds);
}
