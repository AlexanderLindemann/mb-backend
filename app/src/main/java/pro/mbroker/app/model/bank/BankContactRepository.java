package pro.mbroker.app.model.bank;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankContactRepository extends JpaRepository<BankContact, UUID> {

    BankContact findAllByBank(UUID bankId);

    @Override
    Optional<BankContact> findById(UUID id);
}
