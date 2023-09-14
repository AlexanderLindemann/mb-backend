package pro.mbroker.app.exception;

import lombok.NonNull;

public class UnknownPermissionValueException extends RestException{
    public UnknownPermissionValueException(@NonNull String message, Exception e) {
        super(message);
    }
}
