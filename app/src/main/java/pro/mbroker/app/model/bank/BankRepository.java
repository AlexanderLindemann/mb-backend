package pro.mbroker.app.model.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {
    @Override
    void deleteById(UUID id);

    @Query(value = "SELECT COALESCE(MAX(order_number), 0) FROM bank", nativeQuery = true)
    Integer findMaxOrderNumber();
}
