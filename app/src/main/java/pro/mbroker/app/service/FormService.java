package pro.mbroker.app.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface FormService {
    ResponseEntity<ByteArrayResource> generateFormFile(UUID bankApplicationId, UUID borrowerProfileId);
}
