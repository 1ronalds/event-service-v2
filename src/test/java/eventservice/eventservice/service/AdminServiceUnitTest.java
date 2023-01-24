package eventservice.eventservice.service;

import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.impl.AdminServiceImpl;
import eventservice.eventservice.model.EventMinimalDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class AdminServiceUnitTest {
    @Mock
    UserRepository userRepository;

    @Mock
    EventRepository eventRepository;

    @Mock
    EventMapStruct eventMapper;

    @InjectMocks
    AdminServiceImpl service;

    ArrayList<String> usernames = new ArrayList<>();
    List<EventEntity> eventEntityList = new LinkedList<>();
    EventMinimalDto eventMinimalDto;
    UserEntity userEntity;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        usernames.add("admin1");

        RoleEntity roleEntity = new RoleEntity(1L, "admin");
        userEntity = new UserEntity(1L, "admin1", "admin@admin.com", "password123", "Adam", "Leo", roleEntity);
        EventTypeEntity publicTypeEntity = new EventTypeEntity(2L, "private");
        eventEntityList.add(new EventEntity(1L, "Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.parse("24-11-2022 00:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                1, userEntity, publicTypeEntity, new HashSet<>()));
        eventMinimalDto = new EventMinimalDto(1L, "Bicycling contest");
    }

    @Test
    void findAllUsernames(){
        Mockito.when(userRepository.findUsernames()).thenReturn(usernames);

        assertEquals(usernames, service.findAllUsernames());
    }

    // Only difference between this method and original is that original only returns public events while this returns all
    // events. Test checks that private events are found. There are no other logical differences so more tests for deeper
    // functionality aren't necessary.
    @Test
    void findAllEvents(){
        Mockito.when(eventRepository.findAllByCountry(any())).thenReturn(eventEntityList);
        Mockito.when(eventMapper.entityToMinimalDto(any())).thenReturn(eventMinimalDto);

        Assertions.assertEquals(List.of(eventMinimalDto), service.findAllEvents("Latvia", null, null, null));
    }

    @Test
    void changeRole(){
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(userEntity));
        service.changeRole("admin1", "user");

        Mockito.verify(userRepository, times(1)).save(userEntity);
        Assertions.assertEquals("user", userEntity.getRoleEntity().getRole());
    }
}
