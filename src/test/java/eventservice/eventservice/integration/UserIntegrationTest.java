package eventservice.eventservice.integration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eventservice.eventservice.business.handlers.ErrorModel;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import eventservice.eventservice.web.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    UserDto userDto;
    String username;

    @BeforeEach
    void init() {
        RoleDto roleDto = new RoleDto(2L, "user");
        userDto = new UserDto(1L, "User111", "user@user.com", "password123", "Adam", "Leo", roleDto);
        username = "User111";
    }

    @Test
    void findUserDetails() throws Exception {
        //Post to database result that will be requested
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        JsonMapper jm2 = JsonMapper.builder().build();
        String userJson = jm.writeValueAsString(userDto);
        String userJsonExpectedResult = jm2.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson)).andDo(print());

        MvcResult result = mockMvc.perform(get("/v1/users/" + userDto.getUsername())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(userJsonExpectedResult, result.getResponse().getContentAsString());
    }





}
