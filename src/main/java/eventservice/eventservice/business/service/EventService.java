package eventservice.eventservice.business.service;

import eventservice.eventservice.model.EventDto;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface EventService {
    List<EventDto> findAllPublicEvents(String country, String city, Date dateFrom, Date dateTo);

    List<EventDto> findAll();
}
