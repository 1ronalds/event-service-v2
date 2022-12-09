package eventservice.eventservice.business.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ErrorModel {

    private LocalDate timestamp;
    private HttpStatus status;
    private String errorMessage;
    private String message;
    private String path;

}
