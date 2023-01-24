package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.AdminService;
import eventservice.eventservice.model.EventMinimalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapStruct eventMapper;
    private final RoleEntity USER_ROLE = new RoleEntity(2L, "user");
    private final RoleEntity ADMIN_ROLE = new RoleEntity(1L, "admin");

    /**
     * Returns list of all registered users
     * @return ArrayList<String>
     */
    @Override
    public ArrayList<String> findAllUsernames(){
        log.info("Service method findAllUsernames() called");
        return userRepository.findUsernames();
    }

    /**
     * Finds all events with given parameters, including private
     * @param country
     * @param city
     * @param dateFrom
     * @param dateTo
     * @return List<EventMinimalDto>
     */
    @Override
    public List<EventMinimalDto> findAllEvents(String country, String city, LocalDate dateFrom, LocalDate dateTo) {
        LocalDateTime dateTimeFrom, dateTimeTo;

        log.info("Service method findAllEvents() called");

        if (city == null) {
            log.info("findAllPublicEvents service method parameter city is null");
            if (dateFrom != null && dateTo != null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are not null, " +
                        "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
                //search by country and date

                dateTimeFrom = dateFrom.atStartOfDay();
                dateTimeTo = dateTo.atStartOfDay();
                return eventRepository.findAllByCountryAndDateTimeBetween(country, dateTimeFrom, dateTimeTo)
                        .stream()
                        .map(eventMapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country
                return eventRepository.findAllByCountry(country)
                        .stream()
                        .map(eventMapper::entityToMinimalDto)
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
                return eventRepository.findAllByCountryAndCityAndDateTimeBetween(country, city, dateTimeFrom, dateTimeTo)
                        .stream()
                        .map(eventMapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
            if (dateFrom == null && dateTo == null) {
                log.info("findAllPublicEvents service method parameters dateFrom, dateTo are null");
                // search by country and city
                return eventRepository.findAllByCountryAndCity(country, city)
                        .stream()
                        .map(eventMapper::entityToMinimalDto)
                        .collect(Collectors.toList());
            }
        }
        log.info("findAllPublicEvents service method parameters dateFrom, dateTo contain a null value, " +
                "dateFrom: {}, dateTo: {}", dateFrom, dateTo);
        // either dateFrom or dateTo is null
        throw new DateIntervalNotSpecifiedException();
    }

    /**
     * Changes role of user
     * @param username
     * @param role
     */
    @Override
    public void changeRole(String username, String role){
        log.info("Service method changeRole() called");

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        userEntity.setRoleEntity(role.equals("admin") ? ADMIN_ROLE : USER_ROLE);
        userRepository.save(userEntity);
    }
}
