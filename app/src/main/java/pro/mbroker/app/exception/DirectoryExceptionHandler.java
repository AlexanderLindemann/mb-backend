package pro.mbroker.app.exception;

import lombok.NonNull;

public class DirectoryExceptionHandler extends IllegalArgumentException {
    public DirectoryExceptionHandler(@NonNull Class<?> directoryEnumType, @NonNull String code) {
        super("Enum directory type: " + directoryEnumType.getSimpleName() + " doesnt exist code= " + code);
    }
}
