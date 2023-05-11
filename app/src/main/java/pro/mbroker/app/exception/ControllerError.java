package pro.mbroker.app.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
public class ControllerError {
    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private HttpStatus status;

    public HttpStatus getStatus() {
        return status;
    }
}

