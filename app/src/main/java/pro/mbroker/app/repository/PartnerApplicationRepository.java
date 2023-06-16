package pro.mbroker.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, UUID>, JpaSpecificationExecutor<PartnerApplication> {
    @EntityGraph(attributePaths = {"bankApplications.creditProgram", "bankApplications.creditProgram.bank"})
    List<PartnerApplication> findAllByPartner(Partner partner, Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank WHERE pa.partner.id = :partnerId AND pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    List<PartnerApplication> findAllIsActiveByPartnerId(@Param("startDate") Optional<LocalDateTime> startDate,
                                                        @Param("endDate") Optional<LocalDateTime> endDate,
                                                        @Param("partnerId") UUID partnerId,
                                                        Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank WHERE pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    List<PartnerApplication> findAllByIsActiveTrue(@Param("startDate") Optional<LocalDateTime> startDate,
                                                   @Param("endDate") Optional<LocalDateTime> endDate,
                                                   Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank WHERE pa.createdBy = :createBy AND pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    List<PartnerApplication> findAllByCreatedByAndIsActiveTrue(@Param("startDate") Optional<LocalDateTime> startDate,
                                                               @Param("endDate") Optional<LocalDateTime> endDate,
                                                               Integer createBy,
                                                               Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank " +
            "WHERE pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    List<PartnerApplication> findAllByIsActiveTrueAndCreatedAtBetween(
            @Param("startDate") Optional<LocalDateTime> startDate,
            @Param("endDate") Optional<LocalDateTime> endDate,
            Pageable pageable);
}
