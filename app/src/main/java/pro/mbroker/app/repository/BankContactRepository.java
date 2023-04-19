package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.Bank;
import pro.mbroker.app.entity.BankContact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankContactRepository extends JpaRepository<BankContact, UUID> {

    List<BankContact> findAllByBank(Bank bank);

    @Override
    Optional<BankContact> findById(UUID id);
}
