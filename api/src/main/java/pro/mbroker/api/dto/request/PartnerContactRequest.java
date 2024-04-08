package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.NotificationTrigger;

import java.util.List;

@Getter
@Setter
@ToString
public class PartnerContactRequest {

    private String name;

    private String email;

    private List<NotificationTrigger> triggers;
}