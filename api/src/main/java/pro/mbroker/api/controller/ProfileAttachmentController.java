package pro.mbroker.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public interface ProfileAttachmentController {

    @PostMapping("/public/profile/{profileId}/attachment")
    void upload(@PathVariable("profileId") Long profileId, @RequestPart("file") MultipartFile file);

    @DeleteMapping("/public/profile/{profileId}/attachment/{attachmentId}")
    void deleteAttachment(@PathVariable("profileId") Long profileId, @PathVariable("attachmentId") Long attachmentId);

}
