package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.app.entity.BankApplication;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BankApplicationRepository extends JpaRepository<BankApplication, UUID>, JpaSpecificationExecutor<BankApplication> {

    @Query(value = "SELECT bc.email " +
            "FROM BankApplication ba " +
            "JOIN ba.creditProgram cp " +
            "JOIN cp.bank b " +
            "JOIN b.contacts bc " +
            "WHERE ba.id = :bankApplicationId AND bc.isActive = true")
    List<String> getEmailsByBankApplicationId(@Param("bankApplicationId") UUID bankApplicationId);

    List<BankApplication> findAllByApplicationNumberIn(Collection<Integer> applicationNumbers);

    List<BankApplication> findByMainBorrowerId(UUID borrowerId);

    @Query("SELECT ba " +
            "FROM BankApplication ba " +
            "WHERE ba.partnerApplication.id " +
            "IN (SELECT bp.partnerApplication.id " +
            "FROM BorrowerProfile bp " +
            "WHERE bp.id = :borrowerProfileId) AND ba.isActive = true")
    List<BankApplication> findBankApplicationsByBorrowerProfileId(@Param("borrowerProfileId") UUID borrowerProfileId);

}
