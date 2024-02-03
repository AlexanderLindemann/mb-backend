package pro.mbroker.app.service;

public interface CianLoadingFilesService {
    void insertLoadedFileRecord(String tableName, String fileName, Integer recordsCount);

    boolean isFileAlreadyLoaded(String tableName, String fileName);
}
