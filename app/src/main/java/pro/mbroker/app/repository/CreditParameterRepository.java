package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.CreditParameter;

import java.util.UUID;

public interface CreditParameterRepository extends JpaRepository<CreditParameter, UUID> {
}
