package eventservice.eventservice.integration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eventservice.eventservice.model.AuthenticationTokenDto;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import javax.transaction.Transactional;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    UserDto userDto;
    String username;

    String jwt, adminJwt;

    @Value("${jwt.secret-key}")
    String secret;

    @BeforeEach
    void init() throws Exception {
        RoleDto roleDto = new RoleDto(2L, "user");
        userDto = new UserDto(null, "User111", "user@user.com", "password123", "Adam", "Leo", roleDto);
        username = "User111";

        //Generate JWT token for connection
        jwt = "Bearer " + Jwts.builder()
                .setSubject(userDto.getUsername())
                .claim("role","user")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        adminJwt = "Bearer " + Jwts.builder()
                .setSubject("Administrator")
                .claim("role","admin")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        System.out.println("${jwt.secret-key}");

        mockMvc.perform(delete("/v1/users/User111").header("Authorization", adminJwt)).andDo(print());
    }



    @Test
    void findUserDetails() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        String userJson = jm.writeValueAsString(userDto);
        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        mockMvc.perform(get("/v1/users/" + userDto.getUsername()).header("Authorization", jwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("User111"));
    }

    @Test
    void saveUser() throws Exception {
        JsonMapper jm = JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, false).build();
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());
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
        String userJson = jm.writeValueAsString(userDto);

        mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(userJson));

        UserDto user2 = userDto;
        userDto.setName("Ivar");
        String userJson2 = jm.writeValueAsString(user2);

        mockMvc.perform(put("/v1/users/" + userDto.getUsername())
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andDo(print())
                .andExpect(status().isOk());
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
                        .header("Authorization", jwt)
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
                        .header("Authorization", adminJwt)
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

        mockMvc.perform(delete("/v1/users/" + userDto.getUsername()).header("Authorization", jwt)).andExpect(status().isNoContent());
    }

}
