package eventservice.eventservice.business.service;

import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    List<EventMinimalDto> findAllUserCreatedAndOrAttendingEvents(String username, String displayValue, String country, String city, LocalDate dateFrom, LocalDate dateTo);
    List<EventMinimalDto> findAllPublicEvents(String country, String city, LocalDate dateFrom, LocalDate dateTo);
    EventDto findEventInfo(Long eventId);
    EventDto saveEvent(String username, EventDto event);
    EventDto editEvent(String username, Long eventId, EventDto event);
    void deleteEvent(String username, Long eventId);

    void addEventAttendance(String username, Long eventId);

    void removeEventAttendance(String username, Long eventId);
}
