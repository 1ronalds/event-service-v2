package eventservice.eventservice.controller;

import eventservice.eventservice.business.service.AdminService;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.web.controller.AdminController;
import eventservice.eventservice.web.controller.EventController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class AdminControllerTest {

    @Mock
    AdminService service;

    @InjectMocks
    AdminController adminController;

    EventMinimalDto eventMinimalDto;
    ArrayList<String> userList = new ArrayList<>();
    LinkedList<EventMinimalDto> eventMinimalDtoList = new LinkedList<>();

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        eventMinimalDto = new EventMinimalDto(1L, "Bicycling contest");
        eventMinimalDtoList.add(eventMinimalDto);
        userList.add("admin1");
    }

    @Test
    void findAllUsernames(){
        Mockito.when(service.findAllUsernames()).thenReturn(userList);

        assertEquals(ResponseEntity.ok(userList), adminController.findAllUsernames());
    }

    @Test
    void findAllEvents(){
        Mockito.when(service.findAllEvents(any(), any(), any(), any())).thenReturn(eventMinimalDtoList);

        Assertions.assertEquals(ResponseEntity.ok(eventMinimalDtoList), adminController.findAllEvents("Latvia",
                null, null, null));
    }

    @Test
    void changeRole() {
        adminController.changeRole("admin1", "user");

        Mockito.verify(service, times(1)).changeRole("admin1", "user");
    }


}
