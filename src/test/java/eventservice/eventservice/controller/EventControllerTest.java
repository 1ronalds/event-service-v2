package eventservice.eventservice.controller;

import eventservice.eventservice.business.handlers.exceptions.CountryNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDisplayValueException;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.model.EventTypeDto;
import eventservice.eventservice.model.UserMinimalDto;
import eventservice.eventservice.web.controller.EventController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class EventControllerTest {
    @Mock
    EventService service;

    @InjectMocks
    EventController controller;

    EventMinimalDto eventDto1;
    EventMinimalDto eventDto2;
    EventMinimalDto eventDto3;
    EventDto fullEventDto;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        eventDto1 = new EventMinimalDto(1L, "Bicycling contest");

        eventDto2 = new EventMinimalDto(2L, "Theatre");

        eventDto3 = new EventMinimalDto(3L, "Marathon");

        EventTypeDto type = new EventTypeDto(1L, "public");
        UserMinimalDto organiser = new UserMinimalDto(1L, "Administrator");

        fullEventDto = new EventDto(1L, "5km marathon", "marathon", "Latvia", "Liepāja", 100, LocalDateTime.parse("13-12-2023 12:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), 0, organiser, type);

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
        Mockito.when(service.findAllPublicEvents("Latvia", null, LocalDate.of(2020, 11, 12), LocalDate.of(2023, 12, 15)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", null, LocalDate.of(2020, 11, 12), LocalDate.of(2023, 12, 15));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_NotFound(){
        Mockito.when(service.findAllPublicEvents("Latvia", null, LocalDate.of(2023, 12, 14), LocalDate.of(2023, 12, 15)))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", null, LocalDate.of(2023, 12, 14), LocalDate.of(2023, 12, 15));
        assertEquals(ResponseEntity.ok(Collections.emptyList()), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", LocalDate.of(2020, 12, 14), LocalDate.of(2023, 12, 15)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", "Riga", LocalDate.of(2020, 12, 14), LocalDate.of(2023, 12, 15));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_NotFound(){
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", LocalDate.of(2023, 12, 14), LocalDate.of(2023, 12, 15)))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllPublicEvents("Latvia", "Riga", LocalDate.of(2023, 12, 14), LocalDate.of(2023, 12, 15));
        assertEquals(ResponseEntity.ok(Collections.emptyList()), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromSpecified_Exception(){
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", LocalDate.of(2023, 12, 14), null))
                .thenThrow(DateIntervalNotSpecifiedException.class);
        assertThrows(DateIntervalNotSpecifiedException.class,() -> controller.findAllPublicEvents("Latvia", "Riga", LocalDate.of(2023, 12, 14), null));
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateToSpecified_Exception(){
        Mockito.when(service.findAllPublicEvents("Latvia", "Riga", null, LocalDate.of(2023, 12, 14)))
                .thenThrow(DateIntervalNotSpecifiedException.class);
        assertThrows(DateIntervalNotSpecifiedException.class,() -> controller.findAllPublicEvents("Latvia", "Riga",  null, LocalDate.of(2023, 12, 14)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueNotSpecified_Exception(){
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, null, null))
                .thenThrow(InvalidDisplayValueException.class);
        assertThrows(InvalidDisplayValueException.class, () -> controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, null, null));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_dateIntervalNotSpecified_Exception(){
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, LocalDate.now(), null))
                .thenThrow(DateIntervalNotSpecifiedException.class);
        assertThrows(DateIntervalNotSpecifiedException.class, () -> controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, LocalDate.now(), null));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_OnlyCountrySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1, eventDto2);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_OnlyCountrySpecified_NotFound(){
        List<EventMinimalDto> eventList =Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCitySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCitySpecified_NotFound(){
        List<EventMinimalDto> eventList = Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCityAndDateIntervalSpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1, eventDto2, eventDto3);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(3, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCityAndDateIntervalSpecified_NotFound(){
        List<EventMinimalDto> eventList = Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "mine", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_OnlyCountrySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1, eventDto2);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueALl_OnlyCountrySpecified_NotFound(){
        List<EventMinimalDto> eventList =Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCitySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCitySpecified_NotFound(){
        List<EventMinimalDto> eventList = Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCityAndDateIntervalSpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1, eventDto2, eventDto3);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(3, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCityAndDateIntervalSpecified_NotFound(){
        List<EventMinimalDto> eventList = Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_countryNotSpecified_Exception(){
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", null, null, LocalDate.now(), null))
                .thenThrow(CountryNotSpecifiedException.class);
        assertThrows(CountryNotSpecifiedException.class, () -> controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "all", null, null, LocalDate.now(), null));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_OnlyCountrySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1, eventDto2);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_OnlyCountrySpecified_NotFound(){
        List<EventMinimalDto> eventList =Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", null, null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", null, null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCitySpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCitySpecified_NotFound(){
        List<EventMinimalDto> eventList = Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", null, null))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", null, null);
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCityAndDateIntervalSpecified_Found(){
        List<EventMinimalDto> eventList = List.of(eventDto1, eventDto2, eventDto3);
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(3, responseEntity.getBody().size());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCityAndDateIntervalSpecified_NotFound(){
        List<EventMinimalDto> eventList = Collections.emptyList();
        Mockito.when(service.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12)))
                .thenReturn(eventList);
        ResponseEntity<List<EventMinimalDto>> responseEntity = controller.findAllUserCreatedAndOrAttendingEvents("Damian123", "attending", "Latvia", "Riga", LocalDate.of(2021, 11, 12), LocalDate.of(2023, 11, 12));
        assertEquals(ResponseEntity.ok(eventList), responseEntity);
        assertEquals(0, responseEntity.getBody().size());
    }

    //Event create/update/delete/view operations

    @Test
    void findEventInfo() throws Exception {
        Mockito.when(service.findEventInfo(any())).thenReturn(fullEventDto);
        assertEquals(ResponseEntity.ok(fullEventDto), controller.findEventInfo(1L));
    }

    @Test
    void saveEvent() throws Exception {
        Mockito.when(service.saveEvent(any(), any())).thenReturn(fullEventDto);
        assertEquals(ResponseEntity.ok(fullEventDto), controller.saveEvent("Administrator", fullEventDto));
    }

    @Test
    void editEvent() throws Exception {
        Mockito.when(service.editEvent(any(), any(), any())).thenReturn(fullEventDto);
        assertEquals(ResponseEntity.ok(fullEventDto), controller.editEvent("Administrator", 1L, fullEventDto));
    }

    @Test
    void deleteEvent() throws Exception {
        controller.deleteEvent("Administrator", 1L);
        Mockito.verify(service, times(1)).deleteEvent("Administrator", 1L);
    }

}
