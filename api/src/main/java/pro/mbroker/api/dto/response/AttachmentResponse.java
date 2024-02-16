package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
@ToString
public class AttachmentResponse {

    private UUID id;

    private String fileName;

    private String contentType;

    private BigInteger contentLength;
}
