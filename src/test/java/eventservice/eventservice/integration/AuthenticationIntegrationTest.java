package eventservice.eventservice.integration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.model.AuthenticationDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class AuthenticationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @Test
    void authenticate_valid() throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Mockito.when(userRepository.findByUsername("User1234")).thenReturn(Optional.of(new UserEntity(
                null,
                "User1111",
                "user@user.com",
                bCryptPasswordEncoder.encode("SecurePassword"),
                "Adam",
                "Leo",
                new RoleEntity(2L, "user")
        )));

        JsonMapper jm = JsonMapper.builder().build();
        String authenticationDto = jm.writeValueAsString(new AuthenticationDto("User1234", "SecurePassword"));

        mockMvc.perform(post("/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void authenticate_invalid() throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Mockito.when(userRepository.findByUsername("User1234")).thenReturn(Optional.of(new UserEntity(
                null,
                "User1111",
                "user@user.com",
                bCryptPasswordEncoder.encode("SecurePassword"),
                "Adam",
                "Leo",
                new RoleEntity(2L, "user")
        )));

        JsonMapper jm = JsonMapper.builder().build();
        String authenticationDto = jm.writeValueAsString(new AuthenticationDto("User1234", "password123"));

        mockMvc.perform(post("/v1/authenticate")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationDto))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
