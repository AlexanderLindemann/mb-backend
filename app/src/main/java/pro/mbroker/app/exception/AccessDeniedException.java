package pro.mbroker.app.exception;

import lombok.NonNull;

public class AccessDeniedException extends RestException {
    public AccessDeniedException(@NonNull Object params, @NonNull Class<?> itemClass) {
        super("The user with params [" + params + "] does not have the rights to access for " + itemClass.getSimpleName());
    }

    public AccessDeniedException(@NonNull Integer id, @NonNull Class<?> itemClass) {
        super("The user with id: " + id + " does not have the rights to access this " + itemClass.getSimpleName());
    }

    public AccessDeniedException(@NonNull Long attachmentId, @NonNull Class<?> itemClass) {
        super("This attachment with id " + attachmentId + " is not linked to any partner application, therefore the user's permission to download cannot be verified" + itemClass.getSimpleName());
    }
}
