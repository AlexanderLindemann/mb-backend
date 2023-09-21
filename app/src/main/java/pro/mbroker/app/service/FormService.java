package pro.mbroker.app.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FormService {
    ResponseEntity<ByteArrayResource> generateFormFile(UUID borrowerProfileId);

    ResponseEntity<ByteArrayResource> signatureFormFile(UUID borrowerProfileId, MultipartFile signature);
}
