package eventservice.eventservice.service;

import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.impl.EventServiceImpl;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.model.EventTypeDto;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventServiceTest {
    @Mock
    EventMapStruct mapper;

    @Mock
    EventRepository repository;

    @Spy
    @InjectMocks
    EventServiceImpl service;

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

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        eventDto1 = new EventMinimalDto(1L, "Bicycling contest");

        eventDto2 = new EventMinimalDto(2L, "Theatre");

        eventDto3 = new EventMinimalDto(3L, "Marathon");

        eventDto4 = new EventMinimalDto(4L, "TestEvent");

        eventDto5 = new EventMinimalDto(5L, "TestEvent");

        RoleEntity roleEntity = new RoleEntity(2L, "user");

        UserEntity userEntity = new UserEntity(1L, "User", "user@gmail.com", "password", "John", "Doe", roleEntity);
        EventTypeEntity publicTypeEntity = new EventTypeEntity(1L, "public");
        EventTypeEntity privateTypeEntity = new EventTypeEntity(2L, "private");

        eventEntity1 = new EventEntity(1L, "Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, Date.valueOf("2021-12-13"), 1, userEntity, publicTypeEntity);

        eventEntity2 = new EventEntity(2L, "Theatre", "Everyone will be amazed watching this theatre","Latvia",
                "Venstspils", 300, Date.valueOf("2023-12-13"), 1, userEntity, privateTypeEntity);

        eventEntity3 = new EventEntity(3L, "Marathon",
                "Running is good for your health, so join us in this 7km marathon", "Lithuania",
                "Vilnius", 300, Date.valueOf("2022-12-13"), 1, userEntity, publicTypeEntity);

        eventEntity4 = new EventEntity(4L, "TestEvent", "TestEvent","Latvia",
                "Riga", 300, Date.valueOf("2023-12-13"), 1, userEntity, publicTypeEntity);

        eventEntity5 = new EventEntity(5L, "TestEvent", "TestEvent","Latvia",
                "Ventspils", 300, Date.valueOf("2023-12-13"), 1, userEntity, publicTypeEntity);
    }

    @Test
     void findAllPublicEvents_OnlyCountrySpecified_Found(){
        String country = "Latvia";
        Mockito.when(repository.findAllByCountryAndTypeType(country, "public")).thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);
        Mockito.when(mapper.entityToMinimalDto(eventEntity5)).thenReturn(eventDto5);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, null, null);
        assertEquals(List.of(eventDto1, eventDto4, eventDto5), results);

    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_NotFound(){
        String country = "Sweden";
        Mockito.when(repository.findAllByCountryAndTypeType(country, "public")).thenReturn(Collections.emptyList());
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_Found(){
        String country = "Latvia";
        String city = "Riga";
        Mockito.when(repository.findAllByCountryAndTypeTypeAndCity(country, "public", city)).thenReturn(List.of(eventEntity1, eventEntity4));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, null, null);
        assertEquals(List.of(eventDto1, eventDto4), results);


    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_NotFound(){
        String country = "Sweden";
        String city = "Stockholm";
        Mockito.when(repository.findAllByCountryAndTypeTypeAndCity(country, "public", city))
                .thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, null, null);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_Found(){
        String country = "Latvia";
        Date dateFrom = Date.valueOf("2020-11-12");
        Date dateTo = Date.valueOf("2024-11-12");
        Mockito.when(repository.findAllByCountryAndTypeTypeAndDateTimeBetween(country, "public", dateFrom, dateTo))
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
        Date dateFrom = Date.valueOf("2019-11-12");
        Date dateTo = Date.valueOf("2020-11-12");
        Mockito.when(repository.findAllByCountryAndTypeTypeAndDateTimeBetween(country, "public", dateFrom, dateTo))
                .thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllPublicEvents(country, null, dateFrom, dateTo);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_Found(){
        String country = "Latvia";
        Date dateFrom = Date.valueOf("2019-11-12");
        Date dateTo = Date.valueOf("2020-11-12");
        String city = "Riga";

        Mockito.when(repository.findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(country, "public", city, dateFrom, dateTo))
                .thenReturn(List.of(eventEntity1, eventEntity4));
        Mockito.when(mapper.entityToMinimalDto(eventEntity1)).thenReturn(eventDto1);
        Mockito.when(mapper.entityToMinimalDto(eventEntity4)).thenReturn(eventDto4);

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, dateFrom, dateTo);
        assertEquals(List.of(eventDto1, eventDto4), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_NotFound(){
        String country = "Latvia";
        Date dateFrom = Date.valueOf("2001-11-12");
        Date dateTo = Date.valueOf("2002-11-12");
        String city = "Riga";

        Mockito.when(repository.findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(country, "public", city, dateFrom, dateTo))
                .thenReturn(Collections.emptyList());

        List<EventMinimalDto> results = service.findAllPublicEvents(country, city, dateFrom, dateTo);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromSpecified_Exception(){
        String country = "Latvia";
        Date dateFrom = Date.valueOf("2001-11-12");
        String city = "Riga";
        assertThrows(DateIntervalNotSpecifiedException.class, () -> service.findAllPublicEvents(country, city, dateFrom, null));
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateToSpecified_Exception(){
        String country = "Latvia";
        Date dateTo = Date.valueOf("2002-11-12");
        String city = "Riga";
        assertThrows(DateIntervalNotSpecifiedException.class, () -> service.findAllPublicEvents(country, city, null, dateTo));
    }
}
