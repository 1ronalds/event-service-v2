package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapStruct mapper;

    @Override
    public List<EventDto> findAllPublicEvents(String country, String city, Date dateFrom, Date dateTo) {
        if (city == null) {
            if (dateFrom != null && dateTo != null) {
                //search by country and date
                return eventRepository.findAllByCountryAndTypeTypeAndDateTimeBetween(country, "public", dateFrom, dateTo)
                        .stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                // search by country
                return eventRepository.findAllByCountryAndTypeType(country, "public")
                        .stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList());
            }
        } else {
            if (dateFrom != null && dateTo != null) {
                //search by country and city and date
                return eventRepository.findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(country, "public", city, dateFrom, dateTo)
                        .stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                // search by country and city
                return eventRepository.findAllByCountryAndTypeTypeAndCity(country, "public", city)
                        .stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList());
            }
        }
        // either dateFrom or dateTo is null
        throw new DateIntervalNotSpecifiedException();
    }

    @Override
    public List<EventDto> findAll() {
        return eventRepository.findAll().stream().map(mapper::entityToDto).collect(Collectors.toList());
    }
}
