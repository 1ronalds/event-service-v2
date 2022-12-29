package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.connection.CountryCityServiceConnection;
import eventservice.eventservice.business.connection.model.CityDto;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.EventNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDataException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.mapper.UserMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.model.EventTypeDto;
import eventservice.eventservice.model.UserMinimalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final EventTypeDto PUBLIC_EVENT = new EventTypeDto(1L, "public");
    public static final EventTypeDto PRIVATE_EVENT = new EventTypeDto(2L, "private");
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";
    private final EventRepository eventRepository;
    private final CountryCityServiceConnection countryCityServiceConnection;
    private final UserService userService;
    private final EventMapStruct mapper;
    private final UserRepository userRepository;

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
    public List<EventMinimalDto> findAllPublicEvents(String country, String city, LocalDate dateFrom, LocalDate dateTo) {
        LocalDateTime dateTimeFrom, dateTimeTo;

        log.info("findAllPublicEvents service method called");
        if (city == null) {
            log.info("findAllPublicEvents service method parameter city is null");
            if (dateFrom != null && dateTo != null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are not null, " +
                        "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
                //search by country and date

                dateTimeFrom = dateFrom.atStartOfDay();
                dateTimeTo = dateTo.atStartOfDay();
                return eventRepository.findAllByCountryAndTypeTypeAndDateTimeBetween(country, PUBLIC, dateTimeFrom, dateTimeTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country
                return eventRepository.findAllByCountryAndTypeType(country, PUBLIC)
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

                dateTimeFrom = dateFrom.atStartOfDay();
                dateTimeTo = dateTo.atStartOfDay();
                return eventRepository.findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(country, PUBLIC, city, dateTimeFrom, dateTimeTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country and city
                return eventRepository.findAllByCountryAndTypeTypeAndCity(country, PUBLIC, city)
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

    /**
     * returns information about event
     *
     * @param eventId
     * @return EventDto
     */
    @Override
    public EventDto findEventInfo(Long eventId) {
        return mapper.entityToDto(eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new));
    }

    @Override
    public EventDto saveEvent(String username, EventDto event) {
        event.setOrganiser(new UserMinimalDto(userService.findUserDetails(username).getId(),
                userService.findUserDetails(username).getUsername()));
        event.setAttendeeCount(0);

        EventTypeDto publicEvent = new EventTypeDto(1L, PUBLIC);
        EventTypeDto privateEvent = new EventTypeDto(2L, PRIVATE);
        event.setType(event.getType().getType().equals(PUBLIC) ? publicEvent : privateEvent);

        String country = event.getCountry();
        String city = event.getCity();

        if (!countryDoesExist(country) || !cityDoesExist(country, city)) {
            throw new InvalidDataException();
        }

        return mapper.entityToDto(eventRepository.save(mapper.dtoToEntity(event, userRepository)));
        //Mapper requires userRepository to convert minimalDto to full entity
    }

    public Boolean countryDoesExist(String country) {
        return countryCityServiceConnection.getCountries().stream().anyMatch(c -> c.getCountry().equals(country));

    }

    public Boolean cityDoesExist(String country, String city) {
        Long countryId = countryCityServiceConnection.getCountries().stream()
                .filter(c -> c.getCountry().equals(country)).findAny().orElseThrow().getCountryId();

        return countryCityServiceConnection.getCities(countryId).contains(new CityDto(city));
    }

    @Override
    public EventDto editEvent(String username, Long eventId, EventDto event) {
        Optional<EventEntity> history = eventRepository.findById(eventId);
        if (history.isEmpty()) {
            throw new UserNotFoundException();
        }
        event.setAttendeeCount(history.get().getAttendeeCount());

        if (Objects.equals(history.get().getOrganiser().getUsername(), username)) {
            event.setOrganiser(new UserMinimalDto(userService.findUserDetails(username).getId(),
                    userService.findUserDetails(username).getUsername()));
        } else {
            throw new InvalidDataException();
        }

        event.setType(event.getType().getType().equals(PUBLIC) ? PUBLIC_EVENT : PRIVATE_EVENT);

        if (!Objects.equals(event.getCity(), history.get().getCity())) {
            if (!countryDoesExist(event.getCountry()) || !cityDoesExist(event.getCountry(), event.getCity())) {
                throw new InvalidDataException();
            }
        }

        return mapper.entityToDto(eventRepository.save(mapper.dtoToEntity(event, userRepository)));
    }

    @Override
    public void deleteEvent(String username, Long eventId) {
        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException();
        }

        if (Objects.equals(event.get().getOrganiser().getUsername(), username)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new InvalidDataException();
        }
    }

}
