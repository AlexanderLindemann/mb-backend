package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.Partner;

import java.util.UUID;

public interface PartnerRepository extends JpaRepository<Partner, UUID> {

}
