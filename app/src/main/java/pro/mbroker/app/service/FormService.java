package pro.mbroker.app.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface FormService {

    ResponseEntity<ByteArrayResource> generateFormFileHtml(UUID borrowerProfileId, Integer sdId);

    ResponseEntity<ByteArrayResource> signatureFormFileHtml(UUID borrowerProfileId, byte[] signature, Integer sdId);
}
