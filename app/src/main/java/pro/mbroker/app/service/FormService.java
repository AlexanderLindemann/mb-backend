package pro.mbroker.app.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface FormService {
    //todo удалить после тестов
    ResponseEntity<ByteArrayResource> generateFormFileTest(UUID borrowerProfileId, byte[] file);

    ResponseEntity<ByteArrayResource> generateFormFileDocx(UUID borrowerProfileId);

    ResponseEntity<ByteArrayResource> signatureFormFile(UUID borrowerProfileId, byte[] signature);

    void updateGeneratedForm(UUID borrowerProfileId, byte[] form);

    void updateSignatureForm(UUID borrowerProfileId, byte[] form);

    ResponseEntity<ByteArrayResource> generateFormFileHtml(UUID borrowerProfileId);

    ResponseEntity<ByteArrayResource> signatureFormFileHtml(UUID borrowerProfileId, byte[] signature);
}
