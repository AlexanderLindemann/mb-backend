package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.BankApplication;

import java.util.UUID;

public interface BankApplicationRepository extends JpaRepository<BankApplication, UUID> {

}
