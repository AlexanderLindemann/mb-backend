package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerRequest {

    private UUID id;

    private String prefixLink;

    private BorrowerProfileRequest mainBorrower;

    private List<BorrowerProfileRequest> coBorrower;

}
