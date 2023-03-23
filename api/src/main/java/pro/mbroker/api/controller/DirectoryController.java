package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pro.mbroker.api.dto.request.DirectoryRequest;
import pro.mbroker.api.dto.response.DirectoryResponse;

@Api("API справочника")
@RestController
@RequestMapping("/public/directory")
public interface DirectoryController {
    @ApiOperation("Получить все возможные типы ENUM справочника")
    @GetMapping
    DirectoryResponse getDirectory();

    @ApiOperation("обновить теги справочника")
    @PostMapping
    DirectoryResponse updateDirectory(@RequestBody DirectoryRequest directoryRequest);

}
