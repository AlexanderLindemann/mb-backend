package pro.mbroker.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pro.mbroker.app.entity.CreditProgram;

import java.util.List;
import java.util.UUID;

public interface CreditProgramRepository extends JpaRepository<CreditProgram, UUID>, JpaSpecificationExecutor<CreditProgram> {
    List<CreditProgram> findAllByBankId(UUID bankId);

    List<CreditProgram> findAllByIdIn(List<UUID> bankIds);

    @EntityGraph(attributePaths = {"bank"})
    @Query("SELECT cp FROM CreditProgram cp WHERE cp.isActive = true")
    List<CreditProgram> findAllWithBankBy(Pageable pageable);

    @Query("SELECT cp FROM CreditProgram cp JOIN FETCH cp.bank WHERE cp.id IN :creditProgramIds")
    List<CreditProgram> findByIdInWithBank(List<UUID> creditProgramIds);
}
