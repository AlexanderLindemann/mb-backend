package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pro.mbroker.app.entity.PartnerContact;

import java.util.UUID;

public interface PartnerContactRepository extends JpaRepository<PartnerContact, UUID>, JpaSpecificationExecutor<PartnerContact> {
}