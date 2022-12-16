package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.connection.CountryCityServiceConnection;
import eventservice.eventservice.business.connection.model.CityDto;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.EventNotFoundException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CountryCityServiceConnection countryCityServiceConnection;
    private final UserService userService;
    private final EventMapStruct mapper;
    /**
     * Finds all public events based on certain criteria through parameter input
     * @param country - the country, where the event is taking place
     * @param city  - the city, where the event is taking place
     * @param dateFrom - the start of the date interval, which is used to find events
     *                   that are taking place during a certain time period
     * @param dateTo  - the end of the date interval, which is used to find events that
     *                  are taking place during a certain time period
     * */
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
    public EventDto findEventInfo(Long eventId) {
        return mapper.entityToDto(eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new));
    }

    @Override
    public EventDto saveEvent(String username, EventDto event) {
        event.setOrganiser(userService.findUserDetails(username));
        event.setAttendeeCount(0);
        //invalid event information exception is thrown in ExceptionHandlerMethods class

        String country = event.getCountry();
        String city = event.getCity();

        if(!countryDoesExist(country)){
            throw new RuntimeException();
        }

        if(!cityDoesExist(country, city)){
            throw new RuntimeException();
        }

        return mapper.entityToDto(eventRepository.save(mapper.dtoToEntity(event)));
    }

    public Boolean countryDoesExist(String country) {
        return countryCityServiceConnection.getCountries().stream().anyMatch(c -> c.getCountry().equals(country));

    }

    public Boolean cityDoesExist(String country, String city) {
        Long countryId = countryCityServiceConnection.getCountries().stream()
                .filter(c -> c.getCountry().equals(country)).findAny().orElseThrow().getCountryId();

        return countryCityServiceConnection.getCities(countryId).contains(new CityDto(city));
    }
}
