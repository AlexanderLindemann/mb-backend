package pro.mbroker.app.exception;

import lombok.NonNull;

public class RestException extends RuntimeException {


    public RestException(@NonNull String message) {
        super(message);
    }

}
