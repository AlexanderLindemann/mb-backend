package pro.mbroker.app.service;

import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;
import pro.mbroker.app.entity.Attachment;
import pro.mbroker.app.entity.BorrowerDocument;
import pro.mbroker.app.exception.ItemNotFoundException;

public interface AttachmentService {

    /**
     * Загружает файл, возвращает идентификатор соответствующего вложения.
     *
     * @param file Файл для загрузки.
     * @return Attachment, представляющего загруженный файл.
     */
    Attachment upload(MultipartFile file);

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
    BorrowerDocument uploadDocument(BorrowerDocumentRequest documentDto);

    /**
     * Получает объект вложения по идентификатору.
     *
     * @param attachmentId Идентификатор вложения.
     * @return Объект вложения, соответствующий идентификатору.
     * @throws ItemNotFoundException если нет вложения с указанным идентификатором.
     */
    Attachment getAttachmentById(Long attachmentId);
}
