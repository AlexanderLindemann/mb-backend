package pro.mbroker.api.dto.request;

import lombok.Data;
import pro.mbroker.api.enums.BankApplicationStatus;

import java.util.Map;

@Data
public class NotificationStatusRequest {

    private Map<Integer,BankApplicationStatus> applications;

}
