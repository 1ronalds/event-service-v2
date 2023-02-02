package eventservice.eventservice.service;

import eventservice.eventservice.business.handlers.exceptions.InvalidUsernamePasswordException;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.impl.AuthenticationServiceImpl;
import eventservice.eventservice.model.AuthenticationDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;


//SpringBootTest is used because otherwise @Value doesn't work in service class
@RunWith(SpringRunner.class)
@SpringBootTest
class AuthenticationServiceUnitTest {

    @Autowired
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    UserRepository userRepository;

    @Test
    void authenticate_valid(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Mockito.when(userRepository.findByUsername("User1111")).thenReturn(Optional.of(
                new UserEntity(
                        null,
                        "User1111",
                        "user@user.com",
                        bCryptPasswordEncoder.encode("password123"),
                        "Adam",
                        "Leo",
                        new RoleEntity(2L, "user"))));

        Assertions.assertDoesNotThrow(() -> authenticationService.authenticate(new AuthenticationDto(
                                "User1111",
                                "password123")));
    }

    @Test
    void authenticate_invalid(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Mockito.when(userRepository.findByUsername("User1111")).thenReturn(Optional.of(
                new UserEntity(
                        null,
                        "User1111",
                        "user@user.com",
                        bCryptPasswordEncoder.encode("password123"),
                        "Adam",
                        "Leo",
                        new RoleEntity(2L, "user"))));

        Assertions.assertThrows(InvalidUsernamePasswordException.class, () -> authenticationService.authenticate(new AuthenticationDto(
                "User1111",
                "password321")));
    }

}
