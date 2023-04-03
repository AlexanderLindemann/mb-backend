package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.mbroker.api.dto.response.EnumDescription;

import java.util.List;

@Api("API справочника")
@RestController
@RequestMapping("/public/filter")
public interface DirectoryController {
    @ApiOperation("Получить все возможные типы ENUM справочника")
    @GetMapping("/directory")
    List<EnumDescription> getAllDirectory();

    @ApiOperation("Получить все возможные типы ENUM регионов")
    @GetMapping("/region")
    List<EnumDescription> getAllRegion();

}
