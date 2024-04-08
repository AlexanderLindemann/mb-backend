package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.NotificationTrigger;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PartnerContactResponse {

    private UUID id;

    private String name;

    private String email;

    private List<NotificationTrigger> triggers;
}