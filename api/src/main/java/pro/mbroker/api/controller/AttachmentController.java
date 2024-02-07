package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.request.AttachmentRequest;
import pro.mbroker.api.dto.response.AttachmentInfo;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.dto.response.StorageResponse;
import pro.mbroker.api.enums.DocumentType;

import java.util.List;
import java.util.UUID;

@Api(value = "API Загрузки файлов", tags = "API Загрузки файлов")
@RestController
@RequestMapping("/public/attachment")
public interface AttachmentController {

    @ApiOperation("Получить файл по attachment_id для скачивания")
    @GetMapping("/{attachmentId}/file")
    ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long attachmentId);

    @ApiOperation("Загрузить файл и получить attachmentId")
    @PostMapping()
    StorageResponse upload(@RequestPart("file") MultipartFile file, Integer sdId);

    @ApiOperation("Загрузить документ клиента")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BorrowerDocumentResponse uploadDocument(@RequestPart("file") MultipartFile file,
                                            @RequestParam UUID borrowerProfileId,
                                            @RequestParam DocumentType documentType,
                                            @RequestParam(required = false) UUID bankId,
                                            @RequestParam(required = false) UUID bankApplicationId,
                                            Integer sdId);

    @ApiOperation("удалить загруженный документ клиента")
    @PutMapping(value = "/{attachmentId}/delete_document")
    void deleteDocument(@PathVariable Long attachmentId, Integer sdId);

    @ApiOperation("удалить attachment")
    @DeleteMapping
    ResponseEntity<?> deleteAttachment(@RequestBody AttachmentRequest attachmentRequest,
                                       Integer sdId);

    @ApiOperation("Получить вложения в байткоде")
    @PostMapping("get_files_by_ids")
    List<AttachmentInfo> getConvertedFiles(@RequestBody List<Long> attachmentsIds);
}
