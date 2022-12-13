package eventservice.eventservice.service;

import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.mapper.UserMapStruct;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.business.service.impl.UserServiceImpl;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class UserServiceUnitTest {

    @Mock
    UserMapStruct mapper;

    @Mock
    UserRepository repository;

    @InjectMocks
    UserServiceImpl service;

    UserDto userDto;
    String username;
    String email;
    UserEntity userEntity;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        RoleDto roleDto = new RoleDto(1L, "admin");
        userDto = new UserDto(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleDto);
        RoleEntity roleEntity = new RoleEntity(1L, "admin");
        userEntity = new UserEntity(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleEntity);
        username = "AdminUser";
        email = "admin@admin.com";
    }

    // FindUserDetails() tests

    @Test
    void findUserDetails() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(mapper.entityToDto(userEntity)).thenReturn(userDto);
        Mockito.when(mapper.dtoToEntity(userDto)).thenReturn(userEntity);

        UserDto response = service.findUserDetails(username);
        assertEquals(userDto, response);
    }

    @Test
    void findUserDetailsNonexistent() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findUserDetails(username));
    }

    // saveUser() tests

    @Test
    void saveUser() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(repository.save(userEntity)).thenReturn(userEntity);
        Mockito.when(mapper.entityToDto(userEntity)).thenReturn(userDto);
        Mockito.when(mapper.dtoToEntity(userDto)).thenReturn(userEntity);

        UserDto response = service.saveUser(userDto);
        Mockito.verify(repository, times(1)).save(userEntity);
        assertEquals(userDto, response);
    }

    @Test
    void saveUserInvalidEmail() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.ofNullable(userEntity));

    }

}
