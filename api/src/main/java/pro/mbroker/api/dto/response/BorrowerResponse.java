package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.BorrowerProfileStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerResponse {

    private UUID bankApplicationId;

    private BorrowerProfileResponse mainBorrower;

    private List<BorrowerProfileResponse> coBorrower;

    private BorrowerProfileStatus status;
}
