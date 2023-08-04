package pro.mbroker.app.repository;

import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.mbroker.api.dto.response.NotificationBankLetterResponse;
import pro.mbroker.app.entity.BankApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankApplicationRepository extends JpaRepository<BankApplication, UUID>, JpaSpecificationExecutor<BankApplication> {

    @Query("SELECT new pro.mbroker.api.dto.response.NotificationBankLetterResponse(ba.id, pa.id, " +
            "ba.applicationNumber, bp.id, p.name, re.residentialComplexName, re.address, pa.realEstateType, " +
            "pa.creditPurposeType, cp.programName, mc.realEstatePrice, " +
            "mc.downPayment, ba.monthCreditTerm, bp.lastName, bp.firstName,  bp.middleName) " +
            "FROM BankApplication ba " +
            "JOIN ba.partnerApplication pa " +
            "JOIN ba.creditProgram cp " +
            "JOIN ba.mainBorrower bp " +
            "JOIN pa.realEstate re " +
            "JOIN pa.mortgageCalculation mc " +
            "JOIN re.partner p " +
            "JOIN bp.borrowerDocument bd " +
            "WHERE ba.id = :bankApplicationId")
    Page<NotificationBankLetterResponse> getCustomerInfoForBankLetter(
            @Param("bankApplicationId") UUID bankApplicationId, Pageable pageable);


    @Query(value = "SELECT bc.email " +
            "FROM BankApplication ba " +
            "JOIN ba.creditProgram cp " +
            "JOIN cp.bank b " +
            "JOIN b.contacts bc " +
            "WHERE ba.id = :bankApplicationId AND bc.isActive = true")
    List<String> getEmailsByBankApplicationId(@Param("bankApplicationId") UUID bankApplicationId);

    Optional<BankApplication> findByApplicationNumber(Integer applicationNumber);

    List<BankApplication> findByMainBorrowerId(UUID borrowerId);

}
