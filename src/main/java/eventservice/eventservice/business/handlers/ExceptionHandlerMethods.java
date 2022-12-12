package eventservice.eventservice.business.handlers;

import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@ControllerAdvice
public class ExceptionHandlerMethods {
    @ExceptionHandler(DateIntervalNotSpecifiedException.class)
    protected ResponseEntity<ErrorModel> handleDateIntervalNotSpecified(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), HttpStatus.BAD_REQUEST,
                "Bad request", "Date interval not specified (date_from or date_to is null)", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }
}