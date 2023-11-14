package pro.mbroker.api.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
    private String code;
    private String message;
    private String source;
    private String sourceDescription;
    private String additionalInfo;
   // private Map<String, String> data; //todo не знаю пока что сюда пихать
}
