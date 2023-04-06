package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.Profile;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByOrganizationId(Long organizationId);

}
