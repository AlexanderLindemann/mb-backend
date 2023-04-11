package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.RealEstateAddress;

import java.util.UUID;

public interface RealEstateAddressRepository extends JpaRepository<RealEstateAddress, UUID> {

}
