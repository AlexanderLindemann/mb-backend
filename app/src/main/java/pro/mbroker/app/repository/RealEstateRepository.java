package pro.mbroker.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.RealEstate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RealEstateRepository extends JpaRepository<RealEstate, UUID> {
    @Query("SELECT r FROM RealEstate r JOIN FETCH r.partner WHERE r.id = :id")
    Optional<RealEstate> findRealEstateByIdWithPartner(@Param("id") UUID id);

    @Query("SELECT cp FROM RealEstate r JOIN r.partner p JOIN p.creditPrograms cp JOIN FETCH cp.creditProgramDetail cpd JOIN FETCH cp.creditParameter JOIN FETCH cp.bank WHERE r.id = :id")
    List<CreditProgram> findCreditProgramsWithDetailsAndParametersByRealEstateId(@Param("id") UUID id);
    Page<RealEstate> findAllByPartnerId(UUID partnerId, Pageable pageable);
}
