package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import pro.mbroker.api.enums.DocumentType;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerDocumentRequest {

    private UUID borrowerProfileId;

    private DocumentType documentType;

    private UUID bankId;

}
