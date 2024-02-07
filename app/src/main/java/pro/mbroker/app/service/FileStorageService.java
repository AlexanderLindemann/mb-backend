package pro.mbroker.app.service;

import pro.mbroker.app.entity.FileStorage;

import java.util.UUID;

public interface FileStorageService {

    FileStorage getFileStorage(UUID id);
}