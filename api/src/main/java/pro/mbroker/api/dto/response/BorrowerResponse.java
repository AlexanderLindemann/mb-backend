package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerResponse {
    @NonNull
    private UUID bankApplicationId;

    private BorrowerProfileResponse mainBorrower;

    private List<BorrowerProfileResponse> coBorrower;


}
