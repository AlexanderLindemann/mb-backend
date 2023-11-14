package pro.mbroker.app.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.api.enums.BorrowerProfileStatus;
import pro.mbroker.app.entity.BorrowerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowerProfileRepository extends JpaRepository<BorrowerProfile, UUID> {
    @Query("SELECT b FROM BorrowerProfile b " +
            "LEFT JOIN FETCH b.realEstate " +
            "LEFT JOIN FETCH b.vehicle " +
            "LEFT JOIN FETCH b.employer e " +
            "LEFT JOIN FETCH e.salaryBanks " +
            "WHERE b.id = :id")
    Optional<BorrowerProfile> findByIdWithRealEstateVehicleAndEmployer(@Param("id") UUID id);

    List<BorrowerProfile> findAllByPhoneNumberAndIsActiveTrue(String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE BorrowerProfile bp SET bp.borrowerProfileStatus = :status WHERE bp.id = :profileId")
    void updateBorrowerProfileStatus(@Param("profileId") UUID profileId, @Param("status") BorrowerProfileStatus status);

    @Modifying
    @Query("UPDATE BorrowerProfile bp SET bp.borrowerProfileStatus = :status WHERE bp.id IN :profileIds")
    void updateBorrowerProfilesStatus(@Param("profileIds") List<UUID> profileIds, @Param("status") BorrowerProfileStatus status);

}
