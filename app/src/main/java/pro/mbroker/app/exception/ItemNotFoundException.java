package pro.mbroker.app.exception;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class ItemNotFoundException extends RestException {
    public ItemNotFoundException(@NonNull Class<?> itemClass, @NonNull Object params) {
        super(StringUtils.capitalize(itemClass.getSimpleName()) + " with params [" + params + "] not found or was deleted");
    }

    public ItemNotFoundException(@NonNull Class<?> itemClass, @NonNull UUID id) {
        super(itemClass.getSimpleName() + " with Id=" + id + " not found or was deleted");
    }
}
