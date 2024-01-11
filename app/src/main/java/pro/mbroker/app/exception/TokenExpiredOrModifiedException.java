package pro.mbroker.app.exception;

import io.micrometer.core.lang.NonNull;

public class TokenExpiredOrModifiedException extends RestException {
    public TokenExpiredOrModifiedException(@NonNull String message) {
        super(message);
    }
}