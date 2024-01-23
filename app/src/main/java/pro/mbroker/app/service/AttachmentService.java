package pro.mbroker.app.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.exception.ItemNotFoundException;

import java.util.List;

public interface AttachmentService {

    /**
     * Загружает файл, возвращает идентификатор соответствующего вложения.
     *
     * @param file Файл для загрузки.
     * @return Attachment, представляющего загруженный файл.
     */
    Attachment upload(MultipartFile file, Integer sdId);

    /**
     * Скачивает файл, соответствующий идентификатору вложения.
     *
     * @param attachmentId Идентификатор вложения, для которого требуется получить файл.
     * @return Файл, соответствующий идентификатору вложения.
     * @throws ItemNotFoundException если нет вложения с указанным идентификатором.
     */
    MultipartFile download(Long attachmentId);

    /**
     * Загружает документ для BorrowerProfile на основе предоставленного объекта BorrowerDocumentRequest.
     *
     * @param documentDto Объект BorrowerDocumentRequest, содержащий информацию о документе.
     * @return Сохраненный объект BorrowerDocument, представляющий загруженный документ.
     */
    BorrowerDocument uploadDocument(MultipartFile file,
                                    BorrowerDocumentRequest documentDto, Integer sdId);

    /**
     * Получает объект вложения по идентификатору.
     *
     * @param attachmentId Идентификатор вложения.
     * @return Объект вложения, соответствующий идентификатору.
     * @throws ItemNotFoundException если нет вложения с указанным идентификатором.
     */
    Attachment getAttachmentById(Long attachmentId);

    /**
     * Метод возвращает файл доступный для скачивания из
     * MultipartFile
     *
     * @param attachmentId Идентификатор вложения.
     * @return Объект вложения, соответствующий идентификатору.
     * @throws ItemNotFoundException если нет вложения с указанным идентификатором.
     */
    ResponseEntity<InputStreamResource> downloadFile(Long attachmentId);

    /**
     * Метод помечает файл attachment как удаленный
     *
     * @param attachmentId айди вложения
     */
    void markAttachmentAsDeleted(Long attachmentId, Integer sdId);

    /**
     * Метод возвращает информацию о вложении в байткоде
     *
     * @param attachmentsIds идентификатор вложений
     * @return список преобразованных вложений
     */
    List<AttachmentInfo> getConvertedFiles(List<Long> attachmentsIds);

    BorrowerDocument saveBorrowerDocument(Attachment attachment, BorrowerDocumentRequest documentDto);

    /**
     * Метод помечает файлы attachment как удаленные
     *
     * @param attachmentIds айди вложений
     */
    void markAttachmentsAsDeleted(List<Long> attachmentIds);
}
