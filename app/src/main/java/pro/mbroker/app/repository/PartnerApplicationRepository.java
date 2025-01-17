package pro.mbroker.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.PartnerApplication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, UUID>, JpaSpecificationExecutor<PartnerApplication> {

    @Query("SELECT DISTINCT pa FROM PartnerApplication pa " +
            "JOIN pa.bankApplications ba " +
            "JOIN ba.creditProgram cp " +
            "JOIN cp.bank WHERE pa.partner.id = :partnerId AND pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    Page<PartnerApplication> findAllIsActiveByPartnerId(@Param("startDate") Optional<LocalDateTime> startDate,
                                                        @Param("endDate") Optional<LocalDateTime> endDate,
                                                        @Param("partnerId") UUID partnerId,
                                                        Pageable pageable);

    @Query("SELECT DISTINCT pa FROM PartnerApplication pa " +
            "JOIN pa.bankApplications ba " +
            "JOIN ba.creditProgram cp " +
            "JOIN cp.bank WHERE pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    Page<PartnerApplication> findAllByIsActiveTrue(@Param("startDate") Optional<LocalDateTime> startDate,
                                                   @Param("endDate") Optional<LocalDateTime> endDate,
                                                   Pageable pageable);

    @Query("SELECT DISTINCT pa FROM PartnerApplication pa " +
            "JOIN pa.bankApplications ba " +
            "JOIN ba.creditProgram cp " +
            "JOIN cp.bank WHERE pa.createdBy = :createBy AND pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    Page<PartnerApplication> findAllByCreatedByAndIsActiveTrue(@Param("startDate") Optional<LocalDateTime> startDate,
                                                               @Param("endDate") Optional<LocalDateTime> endDate,
                                                               Integer createBy,
                                                               Pageable pageable);


    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN pa.bankApplications ba " +
            "JOIN ba.creditProgram cp " +
            "JOIN cp.bank " +
            "WHERE pa.isActive = true " +
            "AND (COALESCE(:startDate, null) IS NULL OR pa.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, null) IS NULL OR pa.createdAt <= :endDate)")
    List<PartnerApplication> findAllByIsActiveTrueAndCreatedAtBetween(
            @Param("startDate") Optional<LocalDateTime> startDate,
            @Param("endDate") Optional<LocalDateTime> endDate,
            Pageable pageable);

    @Query("SELECT DISTINCT pa FROM PartnerApplication pa " +
            "JOIN pa.borrowerProfiles bp " +
            "WHERE bp.phoneNumber = :phoneNumber")
    List<PartnerApplication> findByBorrowerPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN pa.borrowerProfiles bp " +
            "JOIN bp.borrowerDocument bd " +
            "JOIN bd.attachment a " +
            "WHERE a.id = :attachmentId")
    Optional<PartnerApplication> findByAttachmentId(@Param("attachmentId") Long attachmentId);

}

