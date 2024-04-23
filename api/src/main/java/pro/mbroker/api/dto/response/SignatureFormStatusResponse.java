package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SignatureFormStatusResponse {

    private boolean isApplicationFullySigned;

    private String statusMessage;

    private List<PartnerContactResponse> contacts = new ArrayList<>();
}