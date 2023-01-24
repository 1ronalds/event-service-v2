package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.connection.CountryCityServiceConnection;
import eventservice.eventservice.business.connection.model.CityDto;
import eventservice.eventservice.business.handlers.exceptions.AttendanceNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DuplicateAttendanceEntryException;
import eventservice.eventservice.business.handlers.exceptions.EventMaxAttendanceException;
import eventservice.eventservice.business.handlers.exceptions.EventNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDataException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static eventservice.eventservice.utils.StringConstants.PUBLIC;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final EventTypeDto PUBLIC_EVENT = new EventTypeDto(1L, "public");
    public static final EventTypeDto PRIVATE_EVENT = new EventTypeDto(2L, "private");
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
        log.info("findEventInfo service method called");
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
        log.info("saveEvent service method called");
        UserDto userDto = userService.findUserDetails(username);
        event.setOrganiser(new UserMinimalDto(userDto.getId(), userDto.getUsername()));
        event.setAttendeeCount(0);

        event.setType(event.getType().getType().equals(PUBLIC) ? PUBLIC_EVENT : PRIVATE_EVENT);

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
        log.info("countryDoesExist service method called");
        return countryCityServiceConnection.getCountries().stream().anyMatch(c -> c.getCountry().equals(country));
    }

    /**
     * Checks if city does exist in remote service
     * @param country
     * @param city
     * @return boolean
     */
    private Boolean cityDoesExist(String country, String city) {
        log.info("cityDoesExist service method called");
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
        log.info("editEvent service method called");
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

        event.setType(event.getType().getType().equals(PUBLIC) ? PUBLIC_EVENT : PRIVATE_EVENT);

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
        log.info("deleteEvent method called");
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
     * @param userId - the id of the user, who is attending the event
     * @param eventId - the id of the event, which the user is attending
     */
    @Override
    public void addEventAttendance(Long userId, Long eventId) {
        log.info("addEventAttendance service method called");
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
     * @param userId - the id of the user, whose attendance is being removed
     * @param eventId - the id of the event
     */
    @Override
    public void removeEventAttendance(Long userId, Long eventId) {
        log.info("removeEventAttendance service method called");
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
