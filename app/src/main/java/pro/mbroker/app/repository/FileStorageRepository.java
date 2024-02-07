package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pro.mbroker.app.entity.FileStorage;

import java.util.UUID;

public interface FileStorageRepository extends JpaRepository<FileStorage, UUID>, JpaSpecificationExecutor<FileStorage> {

}