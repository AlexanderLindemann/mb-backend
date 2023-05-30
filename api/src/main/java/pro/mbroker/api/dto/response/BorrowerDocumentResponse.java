package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.DocumentType;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerDocumentResponse {

    private UUID borrowerProfileId;

    private String attachmentName;

    private Long attachmentId;

    private DocumentType documentType;

    private UUID bankId;

}
