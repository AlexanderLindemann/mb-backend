package pro.mbroker.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.CreditProgram;
import pro.mbroker.app.entity.RealEstate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RealEstateRepository extends JpaRepository<RealEstate, UUID>, JpaSpecificationExecutor<RealEstate> {
    @Query("SELECT r FROM RealEstate r JOIN FETCH r.partner WHERE r.id = :id")
    Optional<RealEstate> findRealEstateByIdWithPartner(@Param("id") UUID id);

    @Query("SELECT cp FROM RealEstate r " +
            "JOIN r.partner p " +
            "JOIN p.creditPrograms cp " +
            "JOIN FETCH cp.creditProgramDetail cpd " +
            "JOIN FETCH cp.creditParameter " +
            "JOIN FETCH cp.bank WHERE r.id = :id " +
            "AND cp.programStartDate <= :currentTime " +
            "AND cp.programEndDate >= :currentTime " +
            "AND cp.isActive = true")
    List<CreditProgram> findCreditProgramsWithDetailsAndParametersByRealEstateId(@Param("id") UUID id, @Param("currentTime") LocalDateTime currentTime);

    Page<RealEstate> findAllByPartnerId(UUID partnerId, Pageable pageable);


    @Query("SELECT r FROM RealEstate r " +
            "JOIN r.partner p " +
            "WHERE r.residentialComplexName = :residentialComplexName " +
            "AND p.id = :partnerId " +
            "AND r.address = :address")
    RealEstate findByResidentialComplexNameAndPartnerId(
            @Param("partnerId") UUID partnerId,
            @Param("residentialComplexName") String residentialComplexName,
            @Param("address") String address);
}
