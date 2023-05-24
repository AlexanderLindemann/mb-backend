package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.dto.request.BorrowerDocumentRequest;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BorrowerProfileResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private List<BorrowerDocumentRequest> documents;


}
