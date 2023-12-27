package pro.mbroker.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.CreditProgram;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CreditProgramRepository extends JpaRepository<CreditProgram, UUID>, JpaSpecificationExecutor<CreditProgram> {
    List<CreditProgram> findAllByBankId(UUID bankId);

    List<CreditProgram> findAllByIdIn(List<UUID> bankIds);

    @Query("SELECT cp FROM CreditProgram cp JOIN FETCH cp.bank b WHERE cp.isActive = true AND b.isActive = true")
    List<CreditProgram> findAllWithBankBy(Pageable pageable);

    @Query("SELECT cp FROM CreditProgram cp JOIN FETCH cp.bank WHERE cp.id IN :creditProgramIds")
    List<CreditProgram> findByIdInWithBank(List<UUID> creditProgramIds);

    @Query("SELECT cp FROM CreditProgram cp JOIN cp.creditProgramDetail  " +
            "WHERE cp.id IN :creditProgramIds and cp.isActive = true")
    List<CreditProgram> findByIdInWithCreditProgramDetail(List<UUID> creditProgramIds);

    @Query("SELECT cp.id FROM CreditProgram cp WHERE cp.isActive = true")
    List<UUID> findAllCreditProgramIds();

    @Query("SELECT cp.cianId FROM CreditProgram cp")
    Set<Integer> findAllCreditProgramCianIds();

    @Query("SELECT cp FROM CreditProgram cp WHERE cp.cianId = :cianId ORDER BY cp.createdAt DESC")
    List<CreditProgram> findByCianIdWithMaxCreatedAt(@Param("cianId") Integer cianId);

}
