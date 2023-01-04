package eventservice.eventservice.integration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import eventservice.eventservice.business.connection.CountryCityServiceConnection;
import eventservice.eventservice.business.connection.model.CityDto;
import eventservice.eventservice.business.connection.model.CountryDto;
import eventservice.eventservice.business.mapper.EventMapStruct;
import eventservice.eventservice.business.repository.EventRepository;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventDto;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.model.EventTypeDto;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import eventservice.eventservice.model.UserMinimalDto;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.context.annotation.AdviceMode.ASPECTJ;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CountryCityServiceConnection countryCityServiceConnection;

    EventMinimalDto eventDto1;
    EventMinimalDto eventDto2;
    EventMinimalDto eventDto3;
    EventMinimalDto eventDto4;
    EventMinimalDto eventDto5;
    EventMinimalDto eventDto6;
    EventEntity eventEntity;
    UserEntity userEntity;



    @BeforeEach
    void init(){
        EventTypeEntity eventTypeEntity = new EventTypeEntity(1L, "public");
        RoleEntity roleEntity = new RoleEntity(1L, "admin");
        userEntity = new UserEntity(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleEntity);
        eventEntity = new EventEntity(7L, "Random contest", "No description", "Latvia", "Liepāja", 10,
                LocalDateTime.of(2023, 1, 1, 12, 00), 0, userEntity, eventTypeEntity);

        eventDto1 = new EventMinimalDto(1L, "Bicycling contest");
        eventDto2 = new EventMinimalDto(2L, "Theatre");
        eventDto3 = new EventMinimalDto(3L, "Marathon");
        eventDto4 = new EventMinimalDto(4L, "Yoga");
        eventDto5 = new EventMinimalDto(5L, "Movie night");
        eventDto6 = new EventMinimalDto(6L, "Kebab eating contest");


    }

    @Test
    void findAllPublicEvents_OnlyCountrySpecified_Found() throws Exception {

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
    void findEventInfo() throws Exception {
        Mockito.when(eventRepository.findById(any())).thenReturn(Optional.of(eventEntity));

        mockMvc.perform(get("/v1/events/event/7"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Random contest"));
    }

    @Test
    void findEventInfoNonexistent() throws Exception {
        Mockito.when(eventRepository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/v1/events/event/7"))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    void saveEvent() throws Exception {
        Mockito.when(eventRepository.findById((anyLong()))).thenReturn(Optional.of(eventEntity));
        Mockito.when(eventRepository.save(any())).thenReturn(Optional.of(eventEntity));
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(df);
        String eventJson = mapper.writeValueAsString(eventEntity);
        // Unnecessary data gets replaced with system generated data
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        Mockito.when(countryCityServiceConnection.getCountries()).thenReturn(List.of(new CountryDto(1L, "Latvia")));
        Mockito.when(countryCityServiceConnection.getCities(any())).thenReturn(List.of(new CityDto("Liepāja")));


        mockMvc.perform(post("/v1/events/user/AdminUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
