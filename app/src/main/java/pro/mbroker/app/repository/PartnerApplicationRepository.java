package pro.mbroker.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.Partner;
import pro.mbroker.app.entity.PartnerApplication;

import java.util.List;
import java.util.UUID;

public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, UUID> {
    @EntityGraph(attributePaths = {"borrowerApplications.creditProgram", "borrowerApplications.creditProgram.bank"})
    List<PartnerApplication> findAllByPartner(Partner partner, Pageable pageable);
}
