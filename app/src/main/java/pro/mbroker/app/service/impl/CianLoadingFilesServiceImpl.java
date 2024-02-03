package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.CianLoadedFile;
import pro.mbroker.app.repository.CianLoadedFilesRepository;
import pro.mbroker.app.service.CianLoadingFilesService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CianLoadingFilesServiceImpl implements CianLoadingFilesService {
    private final CianLoadedFilesRepository cianLoadedFilesRepository;
    @Override
    @Transactional
    public void insertLoadedFileRecord(String tableName, String fileName, Integer recordsCount) {
        CianLoadedFile request = new CianLoadedFile();
        request.setFileName(fileName);
        request.setTableName(tableName);
        request.setRecordsCount(recordsCount);
        request.setCreatedAt(LocalDateTime.now());

        cianLoadedFilesRepository.save(request);
    }

    @Override
    public boolean isFileAlreadyLoaded(String tableName, String fileName) {
        return cianLoadedFilesRepository.existsByTableNameAndFileName(tableName, fileName);
    }
}
