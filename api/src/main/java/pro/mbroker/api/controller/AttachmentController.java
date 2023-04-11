package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "API Загрузки файлов", tags = "API Загрузки файлов")
@RestController
@RequestMapping("/public/attachment")
public interface AttachmentController {
    @ApiOperation("Получить файл по attachment_id")
    @PostMapping("/{attachmentId}")
    MultipartFile download(@PathVariable Long attachmentId);

    @ApiOperation("Загрузить файл и получить attachmentId")
    @PostMapping()
    Long upload(@RequestPart("file") MultipartFile file);
}
