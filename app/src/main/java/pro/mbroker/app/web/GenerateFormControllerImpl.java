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
    public ResponseEntity<ByteArrayResource> generateFormFileHtml(UUID borrowerProfileId, Integer sdId) {
        return formService.generateFormFileHtml(borrowerProfileId, sdId);
    }

    @Override
    public ResponseEntity<ByteArrayResource> signatureFormFileHtml(UUID borrowerProfileId, byte[] signature, Integer sdId) {
        return formService.signatureFormFileHtml(borrowerProfileId, signature, sdId);
    }
}
