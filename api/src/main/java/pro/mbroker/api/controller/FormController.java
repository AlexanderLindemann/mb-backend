package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Api(value = "API Генерации Анкеты", tags = "API Генерации Анкеты")
@RestController
@RequestMapping("/public/form")
public interface FormController {

    @ApiOperation("сгенерировать анкету")
    @PostMapping("/generate-form")
    ResponseEntity<ByteArrayResource> generateFormFile(@RequestParam UUID bankApplicationId, @RequestParam UUID borrowerProfileId, @RequestParam("file") MultipartFile file);

}
