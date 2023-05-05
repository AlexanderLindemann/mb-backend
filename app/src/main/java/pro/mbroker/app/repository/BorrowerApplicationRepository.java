package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.BorrowerApplication;

import java.util.UUID;

public interface BorrowerApplicationRepository extends JpaRepository<BorrowerApplication, UUID> {

}
