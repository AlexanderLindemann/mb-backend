package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api(value = "API Генерации Анкеты", tags = "API Генерации Анкеты")
@RestController
@RequestMapping("/public/generate_form")
public interface GenerateFormController {

    @ApiOperation("сгенерировать анкету из docx в pdf")
    @PostMapping("/generate-borrower-form-docx")
    ResponseEntity<ByteArrayResource> generateFormFileDocx(@RequestParam UUID borrowerProfileId);

    //todo удалить после тестов
    @ApiOperation("сгенерировать анкету (тестова api для Сани)")
    @PostMapping("/generate-borrower-form-test")
    ResponseEntity<ByteArrayResource> generateFormFileTest(@RequestParam UUID borrowerProfileId,
                                                           @RequestBody byte[] file);

    @ApiOperation("подписать анкету")
    @PutMapping("/signature-borrower-form")
    ResponseEntity<ByteArrayResource> signatureFormFile(@RequestParam UUID borrowerProfileId,
                                                        @RequestBody byte[] signature);

    @ApiOperation("Сохранить сгенерированную анкету")
    @PutMapping("/{borrowerProfileId}/update-generated-form")
    void updateGeneratedForm(@ApiParam(value = "Идентификатор профиля") @PathVariable UUID borrowerProfileId,
                             @RequestBody byte[] form);

    @ApiOperation("Сохранить подписанную анкету")
    @PutMapping("/{borrowerProfileId}/signature-generated-form")
    void updateSignatureForm(@ApiParam(value = "Идентификатор профиля") @PathVariable UUID borrowerProfileId,
                             @RequestBody byte[] form);

    @ApiOperation("сгенерировать анкету из html в pdf")
    @PostMapping("/generate-borrower-form-html")
    ResponseEntity<ByteArrayResource> generateFormFileHtml(@RequestParam UUID borrowerProfileId);

    @ApiOperation("подписать анкету html")
    @PostMapping("/signature-borrower-form-html")
    ResponseEntity<ByteArrayResource> signatureFormFileHtml(@RequestParam UUID borrowerProfileId,
                                                            @RequestBody byte[] signature);
}
