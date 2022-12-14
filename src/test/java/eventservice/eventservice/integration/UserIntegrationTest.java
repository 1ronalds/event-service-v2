package eventservice.eventservice.integration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import javax.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        JsonMapper jm2 = JsonMapper.builder().build();
        String userJson = jm.writeValueAsString(userDto);
        String userJsonExpectedResult = jm2.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        MvcResult result = mockMvc.perform(get("/v1/users/" + userDto.getUsername())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(userJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void findUserDetailsNonexistentUser() throws Exception {
        mockMvc.perform(delete("/v1/users/nonexistent"));

        mockMvc.perform(get("/v1/users/" + userDto.getUsername())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void saveUser() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        JsonMapper jm2 = JsonMapper.builder().build();
        String userJson = jm.writeValueAsString(userDto);
        String userJsonExpectedResult = jm2.writeValueAsString(userDto);

        MvcResult result = mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(userJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void saveUserInvalidData() throws Exception {
        userDto.setEmail("incorrect-email");
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveUserRepetitiveEmail() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        JsonMapper jm2 = JsonMapper.builder().build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        UserDto user2 = userDto;
        user2.setUsername("user2");
        // email stays the same
        String userJson2 = jm.writeValueAsString(user2);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveUserRepetitiveUsername() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        JsonMapper jm2 = JsonMapper.builder().build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        UserDto user2 = userDto;
        user2.setEmail("user2@user.com");
        // username stays the same
        String userJson2 = jm.writeValueAsString(user2);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void editUserDetails() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        JsonMapper jm2 = JsonMapper.builder().build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        UserDto user2 = userDto;
        userDto.setName("Ivar");
        String userJson2 = jm.writeValueAsString(user2);
        String userJsonExpectedResult = jm2.writeValueAsString(user2);

        MvcResult result = mockMvc.perform(put("/v1/users/" + userDto.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(userJsonExpectedResult, result.getResponse().getContentAsString());
    }

    @Test
    void editUserDetailsInvalidData() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        UserDto user2 = userDto;
        userDto.setEmail("invalid-email");
        String userJson2 = jm.writeValueAsString(user2);

        mockMvc.perform(put("/v1/users/" + userDto.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void editUserDetailsNonexistentUser() throws Exception {
        mockMvc.perform(delete("/v1/users/nonexistent"));

        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(put("/v1/users/" + userDto.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        mockMvc.perform(delete("/v1/users/" + userDto.getUsername())).andExpect(status().isNoContent());
    }

    @Test
    void deleteUserInvalidUsername() throws Exception {
        mockMvc.perform(delete("/v1/users/nonexistent"));

        mockMvc.perform(delete("/v1/users/nonexistent")).andExpect(status().isNotFound());
    }

}
