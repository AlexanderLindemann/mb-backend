package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.controller.FormController;
import pro.mbroker.app.service.FormService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FormControllerImpl implements FormController {
    private final FormService formService;

    @Override
    public ResponseEntity<ByteArrayResource> generateFormFile(UUID bankApplicationId, UUID borrowerProfileId, MultipartFile file) {
        return formService.generateFormFile(bankApplicationId, borrowerProfileId, file);
    }

}
