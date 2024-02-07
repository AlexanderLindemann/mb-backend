package pro.mbroker.api.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.net.URL;
import java.util.UUID;

@Getter
@Setter
@ToString
public class StorageResponse {

    private UUID id;

    private URL url;

    private String fileName;

    private String contentType;

    private BigInteger contentLength;
}
