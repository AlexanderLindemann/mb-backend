package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.BorrowerProfileStatus;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerProfileRequest {

    private UUID id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private Integer cianUserId;

    private BorrowerProfileStatus status = BorrowerProfileStatus.DATA_NO_ENTERED;

}
