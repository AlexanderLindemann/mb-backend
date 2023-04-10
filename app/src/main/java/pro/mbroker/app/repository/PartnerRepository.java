package pro.mbroker.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.Partner;

import java.util.List;
import java.util.UUID;

public interface PartnerRepository extends JpaRepository<Partner, UUID> {
    @EntityGraph(attributePaths = {"bank"})
    List<Partner> findAllWithBankBy(Pageable pageable);

}
