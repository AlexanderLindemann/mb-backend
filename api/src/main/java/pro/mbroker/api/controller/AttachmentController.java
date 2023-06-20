package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.dto.response.BorrowerDocumentResponse;
import pro.mbroker.api.enums.DocumentType;

import java.util.UUID;

@Api(value = "API Загрузки файлов", tags = "API Загрузки файлов")
@RestController
@RequestMapping("/public/attachment")
public interface AttachmentController {

    @ApiOperation("Получить файл по attachment_id")
    @GetMapping("/{attachmentId}")
    MultipartFile download(@PathVariable Long attachmentId);

    @ApiOperation("Получить файл по attachment_id в base64")
    @GetMapping("/{attachmentId}/attachment_head")
    MultipartFile downloadBase64(@PathVariable Long attachmentId);

    @ApiOperation("Загрузить файл и получить attachmentId")
    @PostMapping()
    Long upload(@RequestPart("file") MultipartFile file);

    @ApiOperation("Загрузить документ клиента")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BorrowerDocumentResponse uploadDocument(@RequestPart("file") MultipartFile file,
                                            @RequestParam UUID borrowerProfileId,
                                            @RequestParam DocumentType documentType,
                                            @RequestParam UUID bankId);

    @ApiOperation("удалить загруженный документ клиента")
    @PutMapping(value = "/{attachmentId}/delete_document")
    void deleteDocument(@PathVariable Long attachmentId);
}
