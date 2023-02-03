package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.connection.CountryCityServiceConnection;
import eventservice.eventservice.business.connection.model.CityDto;
import eventservice.eventservice.business.handlers.exceptions.CountryNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.AttendanceNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DuplicateAttendanceEntryException;
import eventservice.eventservice.business.handlers.exceptions.EventMaxAttendanceException;
import eventservice.eventservice.business.handlers.exceptions.EventNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDataException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDisplayValueException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.enums.DisplayType;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.model.EventTypeDto;
import eventservice.eventservice.model.UserDto;
import eventservice.eventservice.model.UserMinimalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static eventservice.eventservice.business.utils.StringConstants.PUBLIC;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    public static final EventTypeDto PUBLIC_EVENT_DTO = new EventTypeDto(1L, "public");
    public static final EventTypeDto PRIVATE_EVENT_DTO = new EventTypeDto(2L, "private");

    public static final EventTypeEntity PUBLIC_EVENT_ENTITY = new EventTypeEntity(1L, "public");

    public static final EventTypeEntity PRIVATE_EVENT_ENTITY = new EventTypeEntity(2L, "private");
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
        LocalDateTime dateTimeFrom = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime dateTimeTo = dateTo != null ? dateTo.atStartOfDay() : null;
        if (city == null) {
            log.info("findAllPublicEvents service method parameter city is null");
            if (dateFrom != null && dateTo != null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are not null, " +
                        "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
                //search by country and date
                return eventRepository.findAllByCountryAndEventTypeAndDateTimeBetween(country, PUBLIC_EVENT_ENTITY, dateTimeFrom, dateTimeTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country
                return eventRepository.findAllByCountryAndEventType(country, PUBLIC_EVENT_ENTITY)
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
                return eventRepository.findAllByCountryAndEventTypeAndCityAndDateTimeBetween(country, PUBLIC_EVENT_ENTITY, city, dateTimeFrom, dateTimeTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country and city
                return eventRepository.findAllByCountryAndEventTypeAndCity(country, PUBLIC_EVENT_ENTITY, city)
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
                                                                        String city, LocalDate dateFrom, LocalDate dateTo) {
        String display = displayValue.toUpperCase();

        switch (DisplayType.valueOf(display)) {
            case ATTENDING:
                log.info("findAllUserCreatedAndOrAttendingEvents service method display value parameter - mine");
                return filterEvents(DisplayType.ATTENDING, username, country, city, dateFrom, dateTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            case ALL:
                log.info("findAllUserCreatedAndOrAttendingEvents service method display value parameter - all");
                if (country == null) {
                    log.info("findAllUserCreatedAndOrAttendingEvents service method parameter country is null");
                    throw new CountryNotSpecifiedException();
                }
                List<EventEntity> mineEvents = filterEvents(DisplayType.MINE, username, country, city, dateFrom, dateTo);
                List<EventEntity> attendingEvents = filterEvents(DisplayType.ATTENDING, username, country, city, dateFrom, dateTo);

                Set<EventEntity> allEvents = new HashSet<>();
                allEvents.addAll(mineEvents);
                allEvents.addAll(attendingEvents);

                return allEvents.stream().map(mapper::entityToMinimalDto).collect(Collectors.toList());
            case MINE:
                log.info("findAllUserCreatedAndOrAttendingEvents display value parameter - attending");
                return filterEvents(DisplayType.MINE, username, country, city, dateFrom, dateTo)
                        .stream()
                        .map(mapper::entityToMinimalDto)
                        .collect(Collectors.toList());
        }
        log.info("findAllUserCreatedAndOrAttendingEvents service method variable display {} is invalid", display);
        throw new InvalidDisplayValueException();
    }

    private List<EventEntity> filterEvents(DisplayType display, String username, String country, String city, LocalDate dateFrom, LocalDate dateTo) {
        LocalDateTime dateTimeFrom = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime dateTimeTo = dateTo != null ? dateTo.atStartOfDay() : null;
        List<EventEntity> events;
        switch(display) {
            case MINE:
                if (dateTimeFrom != null && dateTimeTo != null){
                    events = eventRepository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(username, country, city, dateTimeFrom, dateTimeTo);
                } else{
                    events = eventRepository.findAllByOrganiserUsernameAndCountryAndCity(username, country, city);
                }
                break;
            case ATTENDING:
                if (dateTimeFrom != null && dateTimeTo != null){
                    events = eventRepository.findAllAttendingByCountryAndCityAndDateTimeBetween(username, country, city, dateTimeFrom, dateTimeTo);
                } else{
                    events = eventRepository.findAllAttendingByCountryAndCity(username, country, city);
                }
                break;
            default:
                throw new InvalidDisplayValueException();
        }
        return events;
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

    /**
     * saves event and returns EventDto
     * @param username
     * @param event
     * @return EventDto
     */
    @Override
    public EventDto saveEvent(String username, EventDto event) {
        UserDto userDto = userService.findUserDetails(username);
        event.setOrganiser(new UserMinimalDto(userDto.getId(), userDto.getUsername()));
        event.setAttendeeCount(0);

        event.setEventType(event.getEventType().getType().equals(PUBLIC) ? PUBLIC_EVENT_DTO : PRIVATE_EVENT_DTO);

        String country = event.getCountry();
        String city = event.getCity();

        if (!countryDoesExist(country) || !cityDoesExist(country, city)) {
            throw new InvalidDataException();
        }

        return mapper.entityToDto(eventRepository.save(mapper.dtoToEntity(event, userRepository)));
        //Mapper requires userRepository to convert minimalDto to full entity
    }

    /**
     * Checks if country does exist in remote service
     * @param country
     * @return boolean
     */
    private Boolean countryDoesExist(String country) {
        return countryCityServiceConnection.getCountries().stream().anyMatch(c -> c.getCountry().equals(country));
    }

    /**
     * Checks if city does exist in remote service
     * @param country
     * @param city
     * @return boolean
     */
    private Boolean cityDoesExist(String country, String city) {
        Long countryId = countryCityServiceConnection.getCountries().stream()
                .filter(c -> c.getCountry().equals(country)).findAny().orElseThrow().getCountryId();

        return countryCityServiceConnection.getCities(countryId).contains(new CityDto(city));
    }

    /**
     * Edits event information and returns EventDto
     * @param username
     * @param eventId
     * @param event
     * @return EventDto
     */
    @Override
    public EventDto editEvent(String username, Long eventId, EventDto event) {
        Optional<EventEntity> history = eventRepository.findById(eventId);
        if (history.isEmpty()) {
            throw new EventNotFoundException();
        }
        event.setAttendeeCount(history.get().getAttendeeCount());

        if (Objects.equals(history.get().getOrganiser().getUsername(), username)) {
            event.setOrganiser(new UserMinimalDto(userService.findUserDetails(username).getId(),
                    userService.findUserDetails(username).getUsername()));
        } else {
            throw new InvalidDataException();
        }

        event.setEventType(event.getEventType().getType().equals(PUBLIC_EVENT_DTO.getType()) ? PUBLIC_EVENT_DTO : PRIVATE_EVENT_DTO);

        if (!Objects.equals(event.getCity(), history.get().getCity())) {
            if (!countryDoesExist(event.getCountry()) || !cityDoesExist(event.getCountry(), event.getCity())) {
                throw new InvalidDataException();
            }
        }

        return mapper.entityToDto(eventRepository.save(mapper.dtoToEntity(event, userRepository)));
    }

    /**
     * Deletes event
     * @param username
     * @param eventId
     */
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

    /**
     *
     * @param username - username, who is attending the event
     * @param eventId - the id of the event, which the user is attending
     */
    @Override
    public void addEventAttendance(String username, Long eventId) {
        Long userId = userService.findUserDetails(username).getId();
        Optional<EventEntity> optionalEventEntity = eventRepository.findById(eventId);
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

        if (!optionalEventEntity.isPresent()){
            log.info("The event with id {} has not been found", eventId);
            throw new EventNotFoundException();
        }

        if (!optionalUserEntity.isPresent()){
            log.info("The user with id {} has not been found", userId);
            throw new UserNotFoundException();
        }
        EventEntity event = optionalEventEntity.get();

        if (event.getAttendeeCount() + 1 > event.getMaxAttendance()){
            log.info("The max attendance for event with id: {} has been reached", eventId);
            throw new EventMaxAttendanceException();
        }

        UserEntity user = optionalUserEntity.get();

        if (event.getAttendees().contains(user)){
            log.info("Duplicate attendance entry - userId: {}, eventId: {}", userId, eventId);
            throw new DuplicateAttendanceEntryException();
        }

        event.getAttendees().add(user);
        event.setAttendeeCount(event.getAttendeeCount() + 1);
        eventRepository.save(event);
    }

    /**
     *
     * @param username - username, whose attendance is being removed
     * @param eventId - the id of the event
     */
    @Override
    public void removeEventAttendance(String username, Long eventId) {
        Long userId = userService.findUserDetails(username).getId();
        Optional<EventEntity> optionalEventEntity = eventRepository.findById(eventId);
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

        if (!optionalEventEntity.isPresent()){
            log.info("The event with id {} has not been found", eventId);
            throw new EventNotFoundException();
        }

        if (!optionalUserEntity.isPresent()){
            log.info("The user with id {} has not been found", userId);
            throw new UserNotFoundException();
        }
        EventEntity event = optionalEventEntity.get();
        UserEntity user = optionalUserEntity.get();

        if (!event.getAttendees().contains(user)){
            log.info("Attendance entry not found - userId: {}, eventId: {}", userId, eventId);
            throw new AttendanceNotFoundException();
        }

        event.getAttendees().remove(user);
        event.setAttendeeCount(event.getAttendeeCount() - 1);
        eventRepository.save(event);
    }
}
