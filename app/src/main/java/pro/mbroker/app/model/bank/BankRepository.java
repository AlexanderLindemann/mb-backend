package pro.mbroker.app.model.bank;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {
    @Override
    void deleteById(UUID id);
}
