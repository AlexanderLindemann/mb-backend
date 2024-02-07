package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.app.entity.FileStorage;
import pro.mbroker.app.exception.ItemNotFoundException;
import pro.mbroker.app.repository.FileStorageRepository;
import pro.mbroker.app.service.FileStorageService;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageRepository fileStorageRepository;

    @Override
    public FileStorage getFileStorage(UUID fileId) {
        if (Objects.isNull(fileId)) {
            return null;
        }
        return fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new ItemNotFoundException(FileStorage.class, fileId));
    }
}