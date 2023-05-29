package pro.mbroker.app.exception;

import lombok.NonNull;

import java.util.UUID;

public class ItemConflictException extends RestException {
    public ItemConflictException(@NonNull Class<?> itemClass, @NonNull UUID id) {
        super(itemClass.getSimpleName() + " with ID: " + id + " cannot be removed as they are the main borrower.");
    }
}
