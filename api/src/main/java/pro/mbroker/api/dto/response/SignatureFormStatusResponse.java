package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignatureFormStatusResponse {

    private boolean isApplicationFullySigned;

    private String statusMessage;
}