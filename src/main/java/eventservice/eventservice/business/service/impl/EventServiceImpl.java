package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.handlers.exceptions.CountryNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDisplayValueException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventMinimalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final String ATTENDING = "attending";
    public static final String MINE = "mine";
    public static final String ALL = "all";
    private final EventRepository eventRepository;
    private final EventMapStruct mapper;

    /**
     * Finds all public events based on certain criteria through parameter input
     *
     * @param country  - the country, where the event is taking place
     * @param city     - the city, where the event is taking place
     * @param dateFrom - the start of the date interval, which is used to find events
     *                 that are taking place during a certain time period
     * @param dateTo   - the end of the date interval, which is used to find events that
     *                 are taking place during a certain time period
     */
    @Override
    public List<EventMinimalDto> findAllPublicEvents(String country, String city, Date dateFrom, Date dateTo) {
        log.info("findAllPublicEvents service method called");
        if (city == null) {
            log.info("findAllPublicEvents service method parameter city is null");
            if (dateFrom != null && dateTo != null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are not null, " +
                        "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
                //search by country and date
                return eventRepository.findAllByCountryAndTypeTypeAndDateTimeBetween(country, "public", dateFrom, dateTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country
                return eventRepository.findAllByCountryAndTypeType(country, "public")
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
        } else {
            log.info("findAllPublicEvents service method parameter city is not null");
            if (dateFrom != null && dateTo != null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are not null, " +
                        "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
                //search by country and city and date
                return eventRepository.findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(country, "public", city, dateFrom, dateTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country and city
                return eventRepository.findAllByCountryAndTypeTypeAndCity(country, "public", city)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
        }
        log.info("findAllPublicEvents service method parameters dateFrom, dateTo contain a null value, " +
                "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
        // either dateFrom or dateTo is null
        throw new DateIntervalNotSpecifiedException();
    }

    @Override
    public List<EventMinimalDto> findAllUserCreatedAndOrAttendingEvents(String username, String displayValue, String country,
                                                                        String city, Date dateFrom, Date dateTo) {
        String display = displayValue.toLowerCase();
        if (display.equals(MINE)) {
            return filterEvents(display, username, country, city, dateFrom, dateTo)
                    .stream()
                    .map(mapper::entityToMinimalDto)
                    .collect(Collectors.toList());
        } else if (display.equals(ALL)) {
            if (country == null){
                throw new CountryNotSpecifiedException();
            }
            List<EventEntity> mineEvents = filterEvents(MINE, username, country, city, dateFrom, dateTo);
            List<EventEntity> attendingEvents = filterEvents(ATTENDING, username, country, city, dateFrom, dateTo);

            Set<EventEntity> allEvents = new HashSet<>();
            allEvents.addAll(mineEvents);
            allEvents.addAll(attendingEvents);

            return allEvents.stream().map(mapper::entityToMinimalDto).collect(Collectors.toList());
        } else if (display.equals(ATTENDING)) {
            return filterEvents(display, username, country, city, dateFrom, dateTo)
                    .stream()
                    .map(mapper::entityToMinimalDto)
                    .collect(Collectors.toList());
        }
        throw new InvalidDisplayValueException();
    }

    private List<EventEntity> filterEvents(String display, String username, String country, String city, Date dateFrom, Date dateTo) {
        if (country != null) {
            if (city != null) {
                if (dateFrom != null && dateTo != null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(username, country, city, dateFrom, dateTo);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByCountryAndCityAndDateTimeBetween(username, country, city, dateFrom, dateTo);
                    }
                }
                if (dateFrom == null && dateTo == null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndCountryAndCity(username, country, city);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByCountryAndCity(username, country, city);
                    }
                }
            } else {
                if (dateFrom != null && dateTo != null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndCountryAndDateTimeBetween(username, country, dateFrom, dateTo);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByCountryAndDateTimeBetween(username, country, dateFrom, dateTo);
                    }
                }
                if (dateFrom == null && dateTo == null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndCountry(username, country);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByCountry(username, country);
                    }
                }
            }
        } else {
            if (city != null) {
                if (dateFrom != null && dateTo != null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndCityAndDateTimeBetween(username, city, dateFrom, dateTo);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByCityAndDateTimeBetween(username, city, dateFrom, dateTo);
                    }
                }
                if (dateFrom == null && dateTo == null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndCity(username, city);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByCity(username, city);
                    }
                }
            } else {
                if (dateFrom != null && dateTo != null) {
                    if (display.equals(MINE)) {
                        return eventRepository.findAllByOrganiserUsernameAndDateTimeBetween(username, dateFrom, dateTo);
                    } else if (display.equals(ATTENDING)) {
                        return eventRepository.findAllAttendingByDateTimeBetween(username, dateFrom, dateTo);
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}
