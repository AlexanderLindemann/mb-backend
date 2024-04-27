package pro.mbroker.api.dto.request.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.NotificationTrigger;

import java.util.UUID;

@Getter
@Setter
@ToString
public class NotificationPartnerLetterRequest {

    private UUID borrowerId;
    private NotificationTrigger trigger;
}