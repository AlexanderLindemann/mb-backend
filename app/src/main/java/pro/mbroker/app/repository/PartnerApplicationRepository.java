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

    @EntityGraph(attributePaths = {"bankApplications.creditProgram", "bankApplications.creditProgram.bank"})
    @Query("SELECT pa FROM PartnerApplication pa WHERE pa.id IN (SELECT pa.id FROM PartnerApplication pa WHERE pa.partner.id = :partnerId AND pa.isActive = true)")
    List<PartnerApplication> findAllIsActiveByPartnerId(@Param("partnerId") UUID partnerId, Pageable pageable);

    @EntityGraph(attributePaths = {"bankApplications.creditProgram", "bankApplications.creditProgram.bank"})
    List<PartnerApplication> findAllByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"bankApplications.creditProgram", "bankApplications.creditProgram.bank"})
    List<PartnerApplication> findAllByCreatedByAndIsActiveTrue(Integer createBy, Pageable pageable);

}
