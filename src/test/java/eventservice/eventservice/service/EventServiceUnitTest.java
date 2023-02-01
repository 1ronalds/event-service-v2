package eventservice.eventservice.service;

import eventservice.eventservice.business.connection.CountryCityServiceConnection;
import eventservice.eventservice.business.connection.model.CityDto;
import eventservice.eventservice.business.connection.model.CountryDto;
import eventservice.eventservice.business.handlers.exceptions.CountryNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.EventNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDataException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.business.service.impl.EventServiceImpl;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.model.EventTypeDto;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import eventservice.eventservice.model.UserMinimalDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class EventServiceUnitTest {
    @Mock
    EventMapStruct mapper;

    @Mock
    EventRepository repository;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    CountryCityServiceConnection countryCityServiceConnection;

    @Spy
    @InjectMocks
    EventServiceImpl service;

    EventDto eventDto;
    UserMinimalDto userMinimalDto;
    EventMinimalDto eventDto1;
    EventMinimalDto eventDto2;
    EventMinimalDto eventDto3;
    EventMinimalDto eventDto4;
    EventMinimalDto eventDto5;
    EventEntity eventEntity1;
    EventEntity eventEntity2;
    EventEntity eventEntity3;
    EventEntity eventEntity4;
    EventEntity eventEntity5;
    LinkedList<CountryDto> countryList = new LinkedList<>();
    LinkedList<CityDto> cityList = new LinkedList<>();
    UserDto userDto;
    UserEntity userEntity2;
    EventDto eventDtoEdited;
    EventEntity eventEntity;
    EventEntity eventEntityEdited;

    EventTypeEntity publicTypeEntity;

    EventTypeEntity privateTypeEntity;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        RoleDto roleDto = new RoleDto(1L, "admin");
        userDto = new UserDto(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleDto);
        userMinimalDto = new UserMinimalDto(1L, "User");
        RoleEntity roleEntity2 = new RoleEntity(1L, "admin");

        userEntity2 = new UserEntity(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleEntity2);
        EventTypeDto publicTypeDto = new EventTypeDto(1L, "public");

        eventDto = new EventDto(1L, "Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.parse("24-11-2022 00:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                1, userMinimalDto, publicTypeDto, new HashSet<>());
        eventDtoEdited = new EventDto(1L, "A Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.parse("24-11-2022 00:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                1, userMinimalDto, publicTypeDto, new HashSet<>());
        eventEntity = new EventEntity(1L, "Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.parse("24-11-2022 00:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                1, userEntity2, publicTypeEntity, new HashSet<>());

        eventDto1 = new EventMinimalDto(1L, "Bicycling contest");

        eventDto2 = new EventMinimalDto(2L, "Theatre");

        eventDto3 = new EventMinimalDto(3L, "Marathon");

        eventDto4 = new EventMinimalDto(4L, "TestEvent");

        eventDto5 = new EventMinimalDto(5L, "TestEvent");

        RoleEntity roleEntity = new RoleEntity(2L, "user");

        UserEntity userEntity = new UserEntity(1L, "User", "user@gmail.com", "password", "John", "Doe", roleEntity);
        publicTypeEntity = new EventTypeEntity(1L, "public");
        privateTypeEntity = new EventTypeEntity(2L, "private");

        eventEntity1 = new EventEntity(1L, "Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.parse("13-12-2023 12:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                1, userEntity, publicTypeEntity, Collections.emptySet());

        eventEntity2 = new EventEntity(2L, "Theatre", "Everyone will be amazed watching this theatre","Latvia",
                "Venstspils", 300, LocalDateTime.parse("13-12-2023 12:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), 1, userEntity, privateTypeEntity, Collections.emptySet());

        eventEntity3 = new EventEntity(3L, "Marathon",
                "Running is good for your health, so join us in this 7km marathon", "Lithuania",
                "Vilnius", 300, LocalDateTime.parse("13-12-2023 12:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), 1, userEntity, publicTypeEntity, Collections.emptySet());

        eventEntity4 = new EventEntity(4L, "TestEvent", "TestEvent","Latvia",
                "Riga", 300, LocalDateTime.parse("13-12-2023 12:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), 1, userEntity, publicTypeEntity, Collections.emptySet());

        eventEntity5 = new EventEntity(5L, "TestEvent", "TestEvent","Latvia",
                "Ventspils", 300, LocalDateTime.parse("13-12-2023 12:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), 1, userEntity, publicTypeEntity, Collections.emptySet());

        CountryDto country = new CountryDto(1L, "Latvia");
        CityDto city = new CityDto("Riga");
        countryList.add(country);
        cityList.add(city);
    }

    @Test
     void findAllPublicEvents_OnlyCountrySpecified_Found(){
        String country = "Latvia";
        Mockito.when(repository.findAllByCountryAndEventType(country, publicTypeEntity)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, null, null);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);

    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_NotFound(){
        String country = "Sweden";
        Mockito.when(repository.findAllByCountryAndEventType(country, publicTypeEntity)).thenReturn(Collections.emptyList());
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_Found(){
        String country = "Latvia";
        String city = "Riga";
        Mockito.when(repository.findAllByCountryAndEventTypeAndCity(country, publicTypeEntity, city)).thenReturn(List.of(eventEntity1, eventEntity4));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, null, null);
        assertEquals(List.of(eventDto1, eventDto4), results);


    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_NotFound(){
        String country = "Sweden";
        String city = "Stockholm";
        Mockito.when(repository.findAllByCountryAndEventTypeAndCity(country, publicTypeEntity, city))
                .thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_Found(){
        String country = "Latvia";
        LocalDate dateFrom = LocalDate.of(2020, 11, 12);
        LocalDate dateTo = LocalDate.of(2024, 11, 12);
        Mockito.when(repository.findAllByCountryAndEventTypeAndDateTimeBetween(any(), any(), any(), any()))
                .thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, dateFrom, dateTo);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_NotFound(){
        String country = "Latvia";
        LocalDate dateFrom = LocalDate.of(2019, 11, 12);
        LocalDate dateTo = LocalDate.of(2020, 11, 12);
        Mockito.when(repository.findAllByCountryAndEventTypeAndDateTimeBetween(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, dateFrom, dateTo);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_Found(){
        String country = "Latvia";
        LocalDate dateFrom = LocalDate.of(2019, 11, 12);
        LocalDate dateTo = LocalDate.of(2020, 11, 12);
        String city = "Riga";

        Mockito.when(repository.findAllByCountryAndEventTypeAndCityAndDateTimeBetween(any(), any(), any(), any(), any()))
                .thenReturn(List.of(eventEntity1, eventEntity4));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, dateFrom, dateTo);
        assertEquals(List.of(eventDto1, eventDto4), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_NotFound(){
        String country = "Latvia";
        LocalDate dateFrom = LocalDate.of(2001, 11, 12);
        LocalDate dateTo = LocalDate.of(2002, 11, 12);
        String city = "Riga";

        Mockito.when(repository.findAllByCountryAndEventTypeAndCityAndDateTimeBetween(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, dateFrom, dateTo);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromSpecified_Exception(){
        String country = "Latvia";
        LocalDate dateFrom = LocalDate.of(2001, 11, 12);
        String city = "Riga";
        assertThrows(DateIntervalNotSpecifiedException.class, () -> service.findAllPublicEvents(country, city, dateFrom, null));
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateToSpecified_Exception(){
        String country = "Latvia";
        LocalDate dateTo = LocalDate.of(2002, 11, 12);
        String city = "Riga";
        assertThrows(DateIntervalNotSpecifiedException.class, () -> service.findAllPublicEvents(country, city, null, dateTo));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_OnlyCountrySpecified_Found(){
        String country = "Latvia";
        String username = "User";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, null)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "mine", country, null, null, null);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_OnlyCountrySpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, null)).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "mine", country, null, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCitySpecified_Found(){
        String country = "Latvia";
        String city = "Riga";
        String username = "User";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, city)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "mine", country, city, null, null);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCitySpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, city)).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "mine", country, city, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCityAndDateIntervalSpecified_Found(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "mine", country, city, LocalDate.now(), LocalDate.now());
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCityAndDateIntervalSpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "mine", country, city, LocalDate.now(), LocalDate.now());
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_OnlyCountrySpecified_Found(){
        String country = "Latvia";
        String username = "User";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, null)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, null)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "all", country, null, null, null);
        assertEquals(List.of(eventDto4, eventDto1, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueALl_OnlyCountrySpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, null)).thenReturn(Collections.emptyList());
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, null)).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "all", country, null, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCitySpecified_Found(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, city)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, city)).thenReturn(List.of(eventEntity1,eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "all", country, city, null, null);
        assertEquals(List.of(eventDto4, eventDto1, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCitySpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCity(username, country, city)).thenReturn(Collections.emptyList());
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, city)).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "all", country, city, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCityAndDateIntervalSpecified_Found(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(List.of(eventEntity1, eventEntity5));
        Mockito.when(repository.findAllAttendingByCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(List.of(eventEntity1, eventEntity4));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "all", country, city, dateStart, dateEnd);
        assertEquals(List.of(eventDto4, eventDto1, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCityAndDateIntervalSpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        Mockito.when(repository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(Collections.emptyList());
        Mockito.when(repository.findAllAttendingByCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "all", country, city, dateStart, dateEnd);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_countryNotSpecified_Exception(){
        Assert.assertThrows(CountryNotSpecifiedException.class, () -> service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", null, null, null, null));
    }

    @Test
    void findEventInfo(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity1));
        Mockito.when(mapper.entityToDto(any())).thenReturn(eventDto);
        assertEquals(eventDto, service.findEventInfo(1L));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_OnlyCountrySpecified_Found(){
        String country = "Latvia";
        String username = "User";
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, null)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "attending", country, null, null, null);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_OnlyCountrySpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, null)).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "attending", country, null, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCitySpecified_Found(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, city)).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "attending", country, city, null, null);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCitySpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        Mockito.when(repository.findAllAttendingByCountryAndCity(username, country, city)).thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "attending", country, city, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCityAndDateIntervalSpecified_Found(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        Mockito.when(repository.findAllAttendingByCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "attending", country, city, dateStart, dateEnd);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCityAndDateIntervalSpecified_NotFound(){
        String country = "Latvia";
        String username = "User";
        String city = "Riga";
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        Mockito.when(repository.findAllAttendingByCountryAndCityAndDateTimeBetween(username, country, city, dateStart.atStartOfDay(), dateEnd.atStartOfDay())).thenReturn(Collections.emptyList());
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllUserCreatedAndOrAttendingEvents(username, "attending", country, city, dateStart, dateEnd);
        assertEquals(Collections.emptyList(), results);
    }


    @Test
    void findEventInfoNonexistentId(){
        Mockito.when(repository.findById(any())).thenThrow(EventNotFoundException.class);
        Mockito.when(mapper.entityToDto(any())).thenReturn(eventDto);
        assertThrows(EventNotFoundException.class, () -> service.findEventInfo(1L));
    }

    @Test
    void saveEvent() {
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(countryCityServiceConnection.getCountries()).thenReturn(countryList);
        Mockito.when(countryCityServiceConnection.getCities(any())).thenReturn(cityList);
        Mockito.when(mapper.dtoToEntity(any(), any())).thenReturn(eventEntity1);
        Mockito.when(mapper.entityToDto(any())).thenReturn(eventDto);
        assertEquals(eventDto, service.saveEvent("User", eventDto));
    }

    @Test
    void saveEventInvalidCountry() {
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(countryCityServiceConnection.getCountries()).thenReturn(Collections.emptyList());
        //passes empty list which is the same as filtering country list and finding no matches
        assertThrows(InvalidDataException.class, () -> service.saveEvent("User", eventDto));
    }

    @Test
    void saveEventInvalidCity(){
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(countryCityServiceConnection.getCities(any())).thenReturn(Collections.emptyList());
        //passes empty list which is the same as filtering city list and finding no matches
        assertThrows(InvalidDataException.class, () -> service.saveEvent("User", eventDto));
    }

    @Test
    void editEvent(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity));
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(mapper.entityToDto(any())).thenReturn(eventDtoEdited);
        Mockito.when(mapper.dtoToEntity(any(), any())).thenReturn(eventEntityEdited);
        Mockito.when(repository.save(any())).thenReturn(eventEntityEdited);

        assertEquals(eventDtoEdited, service.editEvent("AdminUser", 1L, eventDtoEdited));
    }

    @Test
    void editEventNonexistent(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> service.editEvent("Nonexistent", 1L, eventDtoEdited));
    }

    @Test
    void editEventIncorrectUsername(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity));
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(mapper.entityToDto(any())).thenReturn(eventDtoEdited);
        Mockito.when(mapper.dtoToEntity(any(), any())).thenReturn(eventEntityEdited);
        Mockito.when(repository.save(any())).thenReturn(eventEntityEdited);

        assertThrows(InvalidDataException.class, () -> service.editEvent("IncorrectUsername", 1L, eventDtoEdited));
    }

    @Test
    void deleteEvent(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity));

        service.deleteEvent("AdminUser", 1L);
        Mockito.verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEventNonexistent(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> service.deleteEvent("AdminUser", 1L));
    }

    @Test
    void deleteEventInvalidUsername(){
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity));

        assertThrows(InvalidDataException.class, () -> service.deleteEvent("IncorrectUser", 1L));
    }

    @Test
    void addEventAttendance_success(){
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity2));

        service.addEventAttendance("AdminUser", 2L);

        assertTrue(eventEntity.getAttendees().contains(userEntity2));
        assertEquals(2, eventEntity.getAttendeeCount());
    }

    @Test
    void addEventAttendance_EventNotFoundException(){
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity2));
        assertThrows(EventNotFoundException.class, () -> service.addEventAttendance("User", 2L));
    }

    @Test
    void addEventAttendance_UserNotFoundException(){
        Mockito.when(userService.findUserDetails(any())).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> service.addEventAttendance("AdminUser", 2L));
    }

    @Test
    void removeEventAttendance_success(){
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(repository.findById(any())).thenReturn(Optional.ofNullable(eventEntity));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity2));

        service.addEventAttendance("AdminUser", 2L);
        service.removeEventAttendance("AdminUser", 2L);

        assertFalse(eventEntity.getAttendees().contains(userEntity2));
        assertEquals(1, eventEntity.getAttendeeCount());
    }

    @Test
    void removeEventAttendance_EventNotFoundException(){
        Mockito.when(userService.findUserDetails(any())).thenReturn(userDto);
        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity2));

        assertThrows(EventNotFoundException.class, () -> service.removeEventAttendance("AdminUser", 2L));
    }

    @Test
    void removeEventAttendance_UserNotFoundException(){
        Mockito.when(userService.findUserDetails(any())).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> service.removeEventAttendance("AdminUser", 2L));
    }
}










