package pro.mbroker.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api(value = "API Генерации Анкеты", tags = "API Генерации Анкеты")
@RestController
@RequestMapping("/public/generate_form")
public interface GenerateFormController {

    @ApiOperation("сгенерировать анкету из html в pdf")
    @PostMapping("/generate-borrower-form-html")
    ResponseEntity<ByteArrayResource> generateFormFileHtml(@RequestParam UUID borrowerProfileId, Integer sdId);

    @ApiOperation("подписать анкету html")
    @PostMapping("/signature-borrower-form-html")
    ResponseEntity<ByteArrayResource> signatureFormFileHtml(@RequestParam UUID borrowerProfileId,
                                                            @RequestBody byte[] signature, Integer sdId);
}
