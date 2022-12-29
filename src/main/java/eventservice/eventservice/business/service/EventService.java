package eventservice.eventservice.business.service;

import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface EventService {
    List<EventMinimalDto> findAllPublicEvents(String country, String city, Date dateFrom, Date dateTo);

    List<EventMinimalDto> findAllUserCreatedAndOrAttendingEvents(String username, String displayValue, String country, String city, Date dateFrom, Date dateTo);
}
