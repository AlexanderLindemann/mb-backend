package pro.mbroker.api.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnderwritingReport {
    private Integer documentType;
    private String documentName;
    private String documentData; //base64
    private String extension;

    public UnderwritingReport() {}
}
