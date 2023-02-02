package eventservice.eventservice.integration;

import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.model.EventMinimalDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    UserRepository userRepository;

    ArrayList<String> usernames = new ArrayList<>();
    List<EventEntity> eventEntityList = new LinkedList<>();
    EventMinimalDto eventMinimalDto;
    UserEntity userEntity;
    String adminJwt;

    @Value("${jwt.secret-key}")
    String secret;


    @BeforeEach
    void init(){
        adminJwt = "Bearer " + Jwts.builder()
                .setSubject("Administrator")
                .claim("role","admin")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        usernames.add("admin1");
        RoleEntity roleEntity = new RoleEntity(1L, "admin");
        userEntity = new UserEntity(1L, "admin1", "admin@admin.com", "password123", "Adam", "Leo", roleEntity);
        EventTypeEntity publicTypeEntity = new EventTypeEntity(2L, "private");
        eventEntityList.add(new EventEntity(1L, "Bicycling contest", "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.parse("24-11-2022 00:00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                1, userEntity, publicTypeEntity, new HashSet<>()));
    }

    @Test
    void findAllUsernames() throws Exception {
        Mockito.when(userRepository.findUsernames()).thenReturn(usernames);
        mockMvc.perform(get("/v1/admin/users")
                        .header("Authorization", adminJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("admin1"));
    }

    // Only difference between this method and original is that original only returns public events while this returns all
    // events. Test checks that private events are found. There are no other logical differences so more tests for deeper
    // functionality aren't necessary.
    @Test
    void findAllEvents() throws Exception {
        Mockito.when(eventRepository.findAllByCountry(any())).thenReturn(eventEntityList);
        mockMvc.perform(get("/v1/admin/events?country=Latvia")
                        .header("Authorization", adminJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Bicycling contest"));
    }

    @Test
    void changeRole() throws Exception {
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(userEntity));
        mockMvc.perform(put("/v1/admin/change-role/admin1?role=user")
                        .header("Authorization", adminJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
