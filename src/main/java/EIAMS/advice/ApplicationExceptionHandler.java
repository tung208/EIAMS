package EIAMS.advice;

import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.exception.StudentNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentNotFoundException.class)
    public Map<String, String> handleBusinessException(StudentNotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", ex.getMessage());
        return errorMap;
    }

    /**
     * Tất cả các Exception không được khai báo sẽ được xử lý tại đây
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleAllException(Exception ex, WebRequest request) {
        // quá trình kiểm soat lỗi diễn ra ở đây
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject("NOT OK", "Action Fail", ""));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseObject> objectNotFound(EntityNotFoundException exception, WebRequest webRequest){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                        exception.getMessage(),
                        webRequest.getDescription(false)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseObject> handleDataException(DataIntegrityViolationException ex, WebRequest request) {
        // quá trình kiểm soat lỗi diễn ra ở đây
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject("Duplicate entry",
                        ex.getMessage(),
                        request.getDescription(false)));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }
}
