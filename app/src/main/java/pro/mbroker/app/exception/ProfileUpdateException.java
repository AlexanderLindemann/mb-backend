package pro.mbroker.app.exception;

@SuppressWarnings("PMD")
public class ProfileUpdateException extends RestException {
    private String fieldName;

    public ProfileUpdateException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
