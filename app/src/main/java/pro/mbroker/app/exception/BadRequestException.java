package pro.mbroker.app.exception;

import lombok.NonNull;

public class BadRequestException extends RestException {
    public BadRequestException(@NonNull String message) {
        super(message);
    }
}
