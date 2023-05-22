package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerRequest {
    @NonNull
    private UUID bankApplicationId;

    private BorrowerProfileRequest mainBorrower;

    private List<BorrowerProfileRequest> coBorrower;


}
