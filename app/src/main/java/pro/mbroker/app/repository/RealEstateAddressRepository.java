package pro.mbroker.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.RealEstateAddress;

import java.util.UUID;

public interface RealEstateAddressRepository extends JpaRepository<RealEstateAddress, UUID> {
    Page<RealEstateAddress> findAllByPartnerId(UUID partnerId, Pageable pageable);
}
