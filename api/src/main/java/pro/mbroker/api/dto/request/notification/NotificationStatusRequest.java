package pro.mbroker.api.dto.request.notification;

import lombok.Data;

import java.util.Map;

@Data
public class NotificationStatusRequest {

    private Map<Integer, UnderwritingResponse> applications;
}
