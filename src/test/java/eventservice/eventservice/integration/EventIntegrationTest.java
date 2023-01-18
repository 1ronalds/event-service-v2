package eventservice.eventservice.integration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.model.EventMinimalDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;
    EventMinimalDto eventDto1;
    EventMinimalDto eventDto2;
    EventMinimalDto eventDto3;
    EventMinimalDto eventDto4;
    EventMinimalDto eventDto5;
    EventMinimalDto eventDto6;

    EventEntity eventEntity1;
    EventEntity eventEntity2;
    EventEntity eventEntity3;
    EventEntity eventEntity4;
    EventEntity eventEntity5;
    EventEntity eventEntity6;

    EventTypeEntity publicTypeEntity;

    EventTypeEntity privateTypeEntity;

    @BeforeEach
    void init(){
        eventDto1 = new EventMinimalDto(1L, "Bicycling contest");
        eventDto2 = new EventMinimalDto(2L, "Theatre");
        eventDto3 = new EventMinimalDto(3L, "Marathon");
        eventDto4 = new EventMinimalDto(4L, "Yoga");
        eventDto5 = new EventMinimalDto(5L, "Movie night");
        eventDto6 = new EventMinimalDto(6L, "Kebab eating contest");

        UserEntity fourthUser = new UserEntity(4L, "Damian123", "Damian123@gmail.com", "password", "Damian",
                "Blaskowicz", new RoleEntity(2L, "user"));
        UserEntity thirdUser = new UserEntity(3L, "CasualMovieEnjoyer", "thisMyEmail@gmail.com", "h@ck3rM@N", "Movie",
                "Enjoyer", new RoleEntity(2L, "user"));
        UserEntity secondUser = new UserEntity(2L, "BestClientEver", "bestClientEver@gmail.com", "h@ck3rM@N", "Mary",
                "Jackson", new RoleEntity(2L, "user"));

        eventEntity1 = new EventEntity(1L, "Bicycling contest",
                "A contest of bicycling free to watch and participate", "Latvia",
                "Riga", 300, LocalDateTime.of(LocalDate.of(2022, 12 ,8),
                LocalTime.of(12,0)), 2, secondUser, new EventTypeEntity(1L, "public"));

        eventEntity2 = new EventEntity(2L, "Theater", "Everyone will be amazed watching this theatre", "Latvia",
                "Venstspils", 50, LocalDateTime.of(LocalDate.of(2022, 12 ,4), LocalTime.of(15,30)),
                3, thirdUser, new EventTypeEntity(2L, "private"));

        eventEntity3 = new EventEntity(3L, "Marathon", "Running is good for your health, so join our 7km marathon", "Lithuania",
                "Vilnius", 1000, LocalDateTime.of(LocalDate.of(2022, 12 ,1), LocalTime.of(10,30)),
                2, fourthUser, new EventTypeEntity(2L, "private"));

        eventEntity4 = new EventEntity(4L, "Yoga", "Come, join us in a group yoga session.", "Latvia",
                "Venstspils", 200, LocalDateTime.of(LocalDate.of(2022, 11 ,1), LocalTime.of(10,30)),
                20, fourthUser, new EventTypeEntity(1L, "public"));

        eventEntity5 = new EventEntity(5L, "Movie night", "Join us in watching a christmas movie", "Latvia",
                "Riga", 500, LocalDateTime.of(LocalDate.of(2022, 12 ,1), LocalTime.of(10,30)),
                0, fourthUser, new EventTypeEntity(1L, "public"));

        eventEntity6 = new EventEntity(6L, "Kebab eating contest", "Prove to everyone once and for all that you are the best kebab eater in Lithuania!", "Lithuania",
                "Kaunas", 500, LocalDateTime.of(LocalDate.of(2022, 12 ,1), LocalTime.of(10,30)),
                0, fourthUser, new EventTypeEntity(2L, "private"));

        publicTypeEntity = new EventTypeEntity(1L, "public");

        privateTypeEntity = new EventTypeEntity(2L, "private");

    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllByCountryAndEventType("Latvia", publicTypeEntity))
                .thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(List.of(eventDto1, eventDto4, eventDto5));

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                            .param("country", "Latvia")
                            .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_NotFound() throws Exception {
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                            .param("country", "Sweden")
                            .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllByCountryAndEventTypeAndCity("Latvia", publicTypeEntity, "Venstspils"))
                .thenReturn(List.of(eventEntity4));
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(List.of(eventDto4));

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                    .param("country", "Latvia")
                    .param("city", "Venstspils")
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_CountryAndCitySpecified_NotFound() throws Exception{
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                        .param("country", "Lithuania")
                        .param("city", "Kaunas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_Found() throws Exception{
        Mockito.when(eventRepository.findAllByCountryAndEventTypeAndDateTimeBetween("Latvia", publicTypeEntity,
                        LocalDateTime.of(2020, 11, 12, 0, 0),
                        LocalDateTime.of(2023, 11, 12, 0, 0)))
                .thenReturn(List.of(eventEntity1, eventEntity4, eventEntity5));
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(List.of(eventDto1, eventDto4, eventDto5));

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                        .param("country", "Latvia")
                        .param("date_from", "12-11-2020")
                        .param("date_to", "12-11-2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_CountryAndDateFromAndDateToSpecified_NotFound() throws Exception{
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                        .param("country", "Latvia")
                        .param("date_from", "12-11-2029")
                        .param("date_to", "12-11-2029")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_Found() throws Exception{
        Mockito.when(eventRepository.findAllByCountryAndEventTypeAndCityAndDateTimeBetween("Latvia", publicTypeEntity, "Venstspils",
                        LocalDateTime.of(2020, 11, 12, 0, 0),
                        LocalDateTime.of(2023, 11, 12, 0, 0)))
                .thenReturn(List.of(eventEntity4));
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(List.of(eventDto4));

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "12-11-2020")
                        .param("date_to", "12-11-2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromAndDateToSpecified_NotFound() throws Exception{
        JsonMapper jm = JsonMapper.builder().build();
        String eventJsonExpectedResult = jm.writeValueAsString(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/v1/events/event")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "12-11-2029")
                        .param("date_to", "12-11-2029")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(eventJsonExpectedResult, result.getResponse().getContentAsString());

    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateFromSpecified_Exception() throws Exception{
        mockMvc.perform(get("/v1/events/event")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "12-11-2020")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllPublicEvents_CountryAndCityAndDateToSpecified_Exception() throws Exception{
        mockMvc.perform(get("/v1/events/event")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_to", "12-11-2020")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueNotSpecified_Exception() throws Exception {
        mockMvc.perform(get("/v1/events/user/Damian123")
                .param("country", "Lativa")
                .param("city", "Riga")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_dateIntervalNotSpecified_Exception() throws Exception {
        mockMvc.perform(get("/v1/events/user/Damian123")
                .param("display", "mine")
                .param("country", "Latvia")
                .param("city", "Riga")
                .param("date_from", "12/12/2022")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_OnlyCountrySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("Damian123", "Latvia", null))
                .thenReturn(List.of(eventEntity4, eventEntity5));
        mockMvc.perform(get("/v1/events/user/Damian123")
                .param("display", "mine")
                .param("country", "Latvia")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[1].id").value(5))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_OnlyCountrySpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("Damian123", "Spain", null))
                        .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/Damian123")
                        .param("display", "mine")
                        .param("country", "Spain")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCitySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("Damian123", "Latvia", "Riga"))
                        .thenReturn(List.of(eventEntity5));
        mockMvc.perform(get("/v1/events/user/Damian123")
                        .param("display", "mine")
                        .param("country", "Latvia")
                        .param("city", "Riga")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCitySpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("Damian123", "Latvia", "Riga"))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/Damian123")
                        .param("display", "mine")
                        .param("country", "Spain")
                        .param("city", "Madrid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCityAndDateIntervalSpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween("Damian123", "Latvia", "Venstspils",
                LocalDateTime.of(2021, 12, 12, 0, 0),
                LocalDateTime.of(2022, 11, 11, 0, 0)))
                .thenReturn(List.of(eventEntity4));
        mockMvc.perform(get("/v1/events/user/Damian123")
                        .param("display", "mine")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "12-12-2021")
                        .param("date_to", "11-11-2022")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueMine_CountryAndCityAndDateIntervalSpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween("Damian123", "Latvia", "Venstspils",
                        LocalDateTime.of(2021, 12, 29, 0, 0),
                        LocalDateTime.of(2023, 11, 11, 0, 0)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/Damian123")
                        .param("display", "mine")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "29-12-2022")
                        .param("date_to", "11-11-2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_OnlyCountrySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("CasualMovieEnjoyer", "Latvia", null))
                .thenReturn(Collections.emptyList());
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("CasualMovieEnjoyer", "Latvia", null))
                .thenReturn(List.of(eventEntity2));
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("country", "Latvia")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueALl_OnlyCountrySpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("CasualMovieEnjoyer", "Spain", null))
                .thenReturn(Collections.emptyList());
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("CasualMovieEnjoyer", "Spain", null))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("country", "Spain")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCitySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("CasualMovieEnjoyer", "Latvia", "Venstspils"))
                .thenReturn(List.of(eventEntity2));
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("CasualMovieEnjoyer", "Latvia", "Venstspils"))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCitySpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("CasualMovieEnjoyer", "Spain", "Madrid"))
                .thenReturn(Collections.emptyList());
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCity("CasualMovieEnjoyer", "Spain", "Madrid"))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("country", "Spain")
                        .param("city", "Madrid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCityAndDateIntervalSpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCityAndDateTimeBetween("CasualMovieEnjoyer", "Latvia", "Venstspils",
                        LocalDateTime.of(2022, 12, 3, 0, 0),
                        LocalDateTime.of(2023, 12, 12, 0, 0)))
                .thenReturn(Collections.emptyList());
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween("CasualMovieEnjoyer", "Latvia", "Venstspils",
                        LocalDateTime.of(2022, 12, 3, 0, 0),
                        LocalDateTime.of(2023, 12, 12, 0, 0)))
                .thenReturn(List.of(eventEntity2));
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "03-12-2022")
                        .param("date_to", "12-12-2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_CountryAndCityAndDateIntervalSpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCityAndDateTimeBetween("CasualMovieEnjoyer", "Latvia", "Venstspils",
                        LocalDateTime.of(2022, 12, 5, 0, 0),
                        LocalDateTime.of(2023, 12, 12, 0, 0)))
                .thenReturn(Collections.emptyList());
        Mockito.when(eventRepository.findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween("CasualMovieEnjoyer", "Latvia", "Venstspils",
                        LocalDateTime.of(2022, 12, 5, 0, 0),
                        LocalDateTime.of(2023, 12, 12, 0, 0)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .param("date_from", "05-12-2022")
                        .param("date_to", "12-12-2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAll_countryNotSpecified_Exception() throws Exception {
        mockMvc.perform(get("/v1/events/user/CasualMovieEnjoyer")
                        .param("display", "all")
                        .param("city", "Venstspils")
                        .param("date_from", "05-12-2022")
                        .param("date_to", "12-12-2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_OnlyCountrySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("BestClientEver", "Latvia", null))
                        .thenReturn(List.of(eventEntity1));
        mockMvc.perform(get("/v1/events/user/BestClientEver")
                        .param("display", "attending")
                        .param("country", "Latvia")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_OnlyCountrySpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("BestClientEver", "Latvia", null))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/BestClientEver")
                        .param("display", "attending")
                        .param("country", "Spain")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCitySpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("BestClientEver", "Latvia", "Riga"))
                .thenReturn(List.of(eventEntity1));
        mockMvc.perform(get("/v1/events/user/BestClientEver")
                        .param("display", "attending")
                        .param("country", "Latvia")
                        .param("city", "Riga")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCitySpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCity("BestClientEver", "Latvia", "Riga"))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/BestClientEver")
                        .param("display", "attending")
                        .param("country", "Latvia")
                        .param("city", "Venstspils")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCityAndDateIntervalSpecified_Found() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCityAndDateTimeBetween("BestClientEver", "Latvia", "Riga",
                        LocalDateTime.of(2022, 12, 7, 0, 0),
                        LocalDateTime.of(2022, 12, 9, 0, 0)))
                .thenReturn(List.of(eventEntity1));
        mockMvc.perform(get("/v1/events/user/BestClientEver")
                        .param("display", "attending")
                        .param("country", "Latvia")
                        .param("city", "Riga")
                        .param("date_from", "07-12-2022")
                        .param("date_to", "09-12-2022")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllUserCreatedAndOrAttendingEvents_displayValueAttending_CountryAndCityAndDateIntervalSpecified_NotFound() throws Exception {
        Mockito.when(eventRepository.findAllAttendingByCountryAndCityAndDateTimeBetween("CasualMovieEnjoyer", "Latvia", "Venstspils",
                        LocalDateTime.of(2022, 12, 9, 0, 0),
                        LocalDateTime.of(2022, 12, 10, 0, 0)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/events/user/BestClientEver")
                        .param("display", "attending")
                        .param("country", "Latvia")
                        .param("city", "Riga")
                        .param("date_from", "09-12-2022")
                        .param("date_to", "10-12-2022")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
