package pro.mbroker.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.mbroker.app.entity.CianLoadedFile;

public interface CianLoadedFilesRepository extends JpaRepository<CianLoadedFile, Integer> {
    boolean existsByTableNameAndFileName(String tableName, String fileName);

}
