package eventservice.eventservice.business.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorModel> handleEnumConflict(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), HttpStatus.NOT_FOUND,
                "Not Found", "User doesn't exist", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }
}