package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.UUID;

public interface BorrowerProfileRepository extends JpaRepository<BorrowerProfile, UUID> {

}
