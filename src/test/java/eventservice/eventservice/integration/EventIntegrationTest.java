package eventservice.eventservice.integration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import eventservice.eventservice.business.handlers.exceptions.CountryNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.DateIntervalNotSpecifiedException;
import eventservice.eventservice.business.handlers.exceptions.InvalidDisplayValueException;
import eventservice.eventservice.model.EventMinimalDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;
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

    EventMinimalDto eventDto1;
    EventMinimalDto eventDto2;
    EventMinimalDto eventDto3;
    EventMinimalDto eventDto4;
    EventMinimalDto eventDto5;
    EventMinimalDto eventDto6;

    @BeforeEach
    void init(){
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
