package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.AttachmentType;

@Getter
@Setter
@ToString
public class AttachmentRequest {

    private Long id;

    private AttachmentType attachmentType;
}
