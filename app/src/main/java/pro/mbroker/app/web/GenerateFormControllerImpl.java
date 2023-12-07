package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.GenerateFormController;
import pro.mbroker.app.service.FormService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateFormControllerImpl implements GenerateFormController {
    private final FormService formService;

    @Override
    public ResponseEntity<ByteArrayResource> generateFormFileDocx(UUID borrowerProfileId) {
        return formService.generateFormFileDocx(borrowerProfileId);
    }

    //todo удалить после тестов
    @Override
    public ResponseEntity<ByteArrayResource> generateFormFileTest(UUID borrowerProfileId, byte[] file) {
        return formService.generateFormFileTest(borrowerProfileId, file);
    }

    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFile(UUID borrowerProfileId, byte[] signature) {
        return formService.signatureFormFile(borrowerProfileId, signature);
    }

    @Override
    public void updateGeneratedForm(UUID borrowerProfileId, byte[] form) {
        formService.updateGeneratedForm(borrowerProfileId, form);
    }

    @Override
    public void updateSignatureForm(UUID borrowerProfileId, byte[] form) {
        formService.updateSignatureForm(borrowerProfileId, form);
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateFormFileHtml(UUID borrowerProfileId) {
        return formService.generateFormFileHtml(borrowerProfileId);
    }

    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFileHtml(UUID borrowerProfileId, byte[] signature) {
        return formService.signatureFormFileHtml(borrowerProfileId, signature);
    }
}
