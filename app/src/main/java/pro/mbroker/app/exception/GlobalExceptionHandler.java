package pro.mbroker.app.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ItemConflictException.class)
    public ResponseEntity<Object> handleAccessDeniedException(ItemConflictException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();
            if (constraintViolationException.getConstraintName().equals("uk_smart_deal_organization_id")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cannot create partner: smart_deal_organization_id must be unique.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UnknownPermissionValueException.class)
    public ResponseEntity<Object> UnknownPermissionValueExceptionException(UnknownPermissionValueException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<Object> handleReportGenerationException(ReportGenerationException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException ex) {
        ControllerError apiError = new ControllerError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<Object> handleProfileUpdateException(ProfileUpdateException ex) {
        String errorMessage = String.format("Ошибка при обновлении поля '%s' профиля заемщика: %s", ex.getFieldName(), ex.getMessage());
        ControllerError apiError = new ControllerError(errorMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
