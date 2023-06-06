package pro.mbroker.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;
import java.util.UUID;

public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, UUID>, JpaSpecificationExecutor<PartnerApplication> {
    @EntityGraph(attributePaths = {"bankApplications.creditProgram", "bankApplications.creditProgram.bank"})
    List<PartnerApplication> findAllByPartner(Partner partner, Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank WHERE pa.partner.id = :partnerId AND pa.isActive = true")
    List<PartnerApplication> findAllIsActiveByPartnerId(@Param("partnerId") UUID partnerId, Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank WHERE pa.isActive = true")
    List<PartnerApplication> findAllByIsActiveTrue(Pageable pageable);

    @Query("SELECT pa FROM PartnerApplication pa " +
            "JOIN FETCH pa.bankApplications ba " +
            "JOIN FETCH ba.creditProgram cp " +
            "JOIN FETCH cp.bank WHERE pa.createdBy = :createdBy AND pa.isActive = true")
    List<PartnerApplication> findAllByCreatedByAndIsActiveTrue(Integer createBy, Pageable pageable);

}
