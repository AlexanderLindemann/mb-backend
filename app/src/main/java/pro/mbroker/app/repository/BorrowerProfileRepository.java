package pro.mbroker.app.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.mbroker.app.entity.BorrowerProfile;

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

    Optional<BorrowerProfile> findBorrowerProfileBySignedFormId(Long id);
}
