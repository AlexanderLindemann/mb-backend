package pro.mbroker.app.exception;

import java.util.UUID;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String entityName, UUID id) {
        super(entityName + " with ID: " + id + " was not found.");
    }

    public DataNotFoundException(String entityName, String additionalInfo) {
        super(entityName + " " + additionalInfo);
    }
}

