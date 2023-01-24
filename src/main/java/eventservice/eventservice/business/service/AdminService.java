package eventservice.eventservice.business.service;

import eventservice.eventservice.model.EventMinimalDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface AdminService {
    ArrayList<String> findAllUsernames();
    List<EventMinimalDto> findAllEvents(String country, String city, LocalDate dateFrom, LocalDate dateTo);
    void changeRole(String username, String role);
}
