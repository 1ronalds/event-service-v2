package eventservice.eventservice.business.handlers;


import eventservice.eventservice.business.handlers.exceptions.AuthorizationException;
import eventservice.eventservice.business.handlers.exceptions.CountryNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.AttendanceNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.CountryNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DuplicateAttendanceEntryException;
import eventservice.eventservice.business.handlers.exceptions.EmailExistsException;
import eventservice.eventservice.business.handlers.exceptions.EventMaxAttendanceException;
import eventservice.eventservice.business.handlers.exceptions.EventNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDataException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDisplayValueException;
import eventservice.eventservice.business.handlers.exceptions.InvalidUsernamePasswordException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.UsernameExistsException;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static eventservice.eventservice.business.utils.StringConstants.BAD_REQUEST;
import static eventservice.eventservice.business.utils.StringConstants.NOT_FOUND;
import static eventservice.eventservice.business.utils.StringConstants.FORBIDDEN;

@ControllerAdvice
public class ExceptionHandlerMethods {

    @ExceptionHandler(DateIntervalNotSpecifiedException.class)
    protected ResponseEntity<ErrorModel> handleDateIntervalNotSpecified(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "Date interval not specified (date_from or date_to is null)", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorModel> handleUserNotFound(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                NOT_FOUND, "User not found", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameExistsException.class)
    protected ResponseEntity<ErrorModel> handleUsernameExists(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "Username already registered", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailExistsException.class)
    protected ResponseEntity<ErrorModel> handleEmailExists(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "Email already registered", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorModel> handleInvalidDataValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = StringUtils.join(ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList())); // Collects validation errors

        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, errors, request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    protected ResponseEntity<ErrorModel> handleEnumConflict(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "Invalid date", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDisplayValueException.class)
    protected ResponseEntity<ErrorModel> handleInvalidDisplayValue(InvalidDisplayValueException ex, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "Invalid display value. Display value should" +
                " be one of the following: all, mine, attending", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CountryNotSpecifiedException.class)
    protected ResponseEntity<ErrorModel> handleCountryNotSpecified(CountryNotSpecifiedException ex, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "Country must be specified", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataException.class)
    protected ResponseEntity<ErrorModel> handleInvalidDataPost(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                "Bad request", "Invalid data format provided", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorModel> handleCannotDeleteDataConnected(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                "Bad request", "Events have been created that have to be deleted to delete user", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    protected ResponseEntity<ErrorModel> handleEventNotFound(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 404,
                NOT_FOUND, "Requested event isn't found", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateAttendanceEntryException.class)
    protected ResponseEntity<ErrorModel> handleDuplicateAttendanceEntry(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 400,
                BAD_REQUEST, "The user is already attending this event", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AttendanceNotFoundException.class)
    protected ResponseEntity<ErrorModel> handleAttendanceNotFound(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 404,
                NOT_FOUND, "There is no attendance entry for the user at this event", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CountryNotFoundException.class)
    protected ResponseEntity<ErrorModel> handleCountryNotFound(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 404,
                "Not Found", "This country does not exist", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventMaxAttendanceException.class)
    protected ResponseEntity<ErrorModel> handleEventMaxAttendance(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 403,
                FORBIDDEN, "The limit of attendees has been reached for this event", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidUsernamePasswordException.class)
    protected ResponseEntity<ErrorModel> handleInvalidUsernamePassword(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 403,
                FORBIDDEN, "Username or password is incorrect", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<ErrorModel> handleInvalidToken(Exception ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), 403,
                FORBIDDEN, "JWT token invalid or expired", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
    }


}