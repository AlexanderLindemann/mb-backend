package pro.mbroker.app.exception;

import lombok.NonNull;

public class AccessDeniedException extends RestException {
    public AccessDeniedException(@NonNull Object params, @NonNull Class<?> itemClass) {
        super("The user with params [" + params + "] does not have the rights to access for " + itemClass.getSimpleName());
    }

    public AccessDeniedException(@NonNull Integer id, @NonNull Class<?> itemClass) {
        super("The user with id: " + id + " does not have the rights to access this " + itemClass.getSimpleName());
    }
}
