package eventservice.eventservice.controller;

import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.web.controller.EventController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class EventControllerTest {
    @Mock
    EventService service;

    @InjectMocks
    EventController controller;

    EventMinimalDto eventDto1;
    EventMinimalDto eventDto2;
    EventMinimalDto eventDto3;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        eventDto1 = new EventMinimalDto(1L, "Bicycling contest");

        eventDto2 = new EventMinimalDto(2L, "Theatre");

        eventDto3 = new EventMinimalDto(3L, "Marathon");
    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllPublicEvents("Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_NotFound(){
        Mockito.when(service.findAllPublicEvents("Sweden", null, null, null))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Sweden", null, null, null);
        assertEquals(ResponseEntity.ok(Collections.emptyList()), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_NotFound(){
        Mockito.when(service.findAllPublicEvents("Lithuania", "Kaunas", null, null))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Lithuania", "Kaunas", null, null);
        assertEquals(ResponseEntity.ok(Collections.emptyList()), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllPublicEvents("Latvia", null, Date.valueOf("2020-11-12"), Date.valueOf("2023-12-15")))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", null, Date.valueOf("2020-11-12"), Date.valueOf("2023-12-15"));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_NotFound(){
        Mockito.when(service.findAllPublicEvents("Latvia", null, Date.valueOf("2023-12-14"), Date.valueOf("2023-12-15")))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", null, Date.valueOf("2023-12-14"), Date.valueOf("2023-12-15"));
        assertEquals(ResponseEntity.ok(Collections.emptyList()), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", Date.valueOf("2020-12-14"), Date.valueOf("2023-12-15")))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", "Riga", Date.valueOf("2020-12-14"), Date.valueOf("2023-12-15"));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_NotFound(){
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", Date.valueOf("2023-12-14"), Date.valueOf("2023-12-15")))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", "Riga", Date.valueOf("2023-12-14"), Date.valueOf("2023-12-15"));
        assertEquals(ResponseEntity.ok(Collections.emptyList()), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromSpecified_Exception(){
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", Date.valueOf("2023-12-14"), null))
                .thenThrow(DateIntervalNotSpecifiedException.class);
        assertThrows(DateIntervalNotSpecifiedException.class,() -> controller.findAllPublicEvents("Latvia", "Riga", Date.valueOf("2023-12-14"), null));
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateToSpecified_Exception(){
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", null, Date.valueOf("2023-12-14")))
                .thenThrow(DateIntervalNotSpecifiedException.class);
        assertThrows(DateIntervalNotSpecifiedException.class,() -> controller.findAllPublicEvents("Latvia", "Riga",  null, Date.valueOf("2023-12-14")));
    }
}
