package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pro.mbroker.app.entity.Bank;

import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID>, JpaSpecificationExecutor<Bank> {
    @Override
    void deleteById(UUID id);

    @Query(value = "SELECT COALESCE(MAX(order_number), 0) FROM bank", nativeQuery = true)
    Integer findMaxOrderNumber();
}
