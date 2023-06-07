package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.DocumentType;

import java.util.UUID;

@Getter
@Setter
@ToString
public class RequiredDocumentResponse {

    private String bankName;

    private UUID bankId;

    private DocumentType documentType;
}
