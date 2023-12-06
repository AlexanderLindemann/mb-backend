package pro.mbroker.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.BorrowerProfileStatus;

import java.time.LocalDateTime;
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

    private String link;

    private Integer cianUserId;

    private BorrowerProfileStatus status;

    private List<BorrowerDocumentResponse> documents;

    @JsonIgnore
    private LocalDateTime createdAt;

}
