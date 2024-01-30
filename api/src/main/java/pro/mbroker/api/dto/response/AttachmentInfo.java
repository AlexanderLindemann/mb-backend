package pro.mbroker.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentInfo {

    private Long attachmentId;
    private byte[] file;
    private String originalFileName;
    private String contentType;
}
