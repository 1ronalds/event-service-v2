package eventservice.eventservice.service;

import eventservice.eventservice.business.handlers.exceptions.EmailExistsException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.UsernameExistsException;
import eventservice.eventservice.business.mapper.UserMapStruct;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.impl.UserServiceImpl;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

public class UserServiceUnitTest {

    @Mock
    UserMapStruct mapper;

    @Mock
    UserRepository repository;

    @Spy
    @InjectMocks
    UserServiceImpl service;

    UserDto userDto;
    String username;
    String email;
    UserEntity userEntity;
    RoleDto roleDto;
    RoleEntity roleEntity;
    UserDto response;

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

        response = service.findUserDetails(username);
        assertEquals(userDto, response);
    }

    @Test
    void findUserDetailsNonexistent() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findUserDetails(username));
    }

    // saveUser() tests, note: invalid data tests are run in integration test

    @Test
    void saveUser() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(repository.save(userEntity)).thenReturn(userEntity);
        Mockito.when(mapper.entityToDto(userEntity)).thenReturn(userDto);
        Mockito.when(mapper.dtoToEntity(userDto)).thenReturn(userEntity);

        response = service.saveUser(userDto);
        Mockito.verify(repository, times(1)).save(userEntity);
        assertEquals(userDto, response);
    }

    @Test
    void saveUserRepetitiveEmail() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.ofNullable(userEntity));

        assertThrows(EmailExistsException.class, () -> service.saveUser(userDto));
    }

    @Test
    void saveUserRepetitiveUsername() throws Exception {
        Mockito.when(repository.findByUsername(username)).thenReturn(Optional.ofNullable(userEntity));

        assertThrows(UsernameExistsException.class, () -> service.saveUser(userDto));
    }

    // editUser() unit tests, note: invalid data tests are run in integration test

    @Test
    void editUser() throws Exception {
        UserDto editDto = new UserDto(1L, "AdminUserEdit", "admin@admin.com", "password123", "Adam", "Leo", roleDto);
        UserEntity editEntity = new UserEntity(1L, "AdminUserEdit", "admin@admin.com", "password123", "Adam", "Leo", roleEntity);

        Mockito.doReturn(userDto).when(service).findUserDetails(username);
        Mockito.when(repository.save(editEntity)).thenReturn(editEntity);
        Mockito.when(mapper.dtoToEntity(editDto)).thenReturn(editEntity);
        Mockito.when(mapper.entityToDto(editEntity)).thenReturn(editDto);

        UserDto result = service.editUser(editDto, username);
        Mockito.verify(repository, times(1)).save(editEntity);
        assertEquals(editDto, result);
    }

    @Test
    void editUserNonexistentUser() throws Exception {
        Mockito.doThrow(UserNotFoundException.class).when(service).findUserDetails(username);

        assertThrows(UserNotFoundException.class, () -> service.editUser(userDto, username));
    }

    // deleteUser() tests

    @Test
    void deleteUser() throws Exception {
        Mockito.doReturn(userDto).when(service).findUserDetails(username);

        service.deleteUser(username);
        Mockito.verify(repository, times(1)).deleteById(userDto.getId());
    }

    @Test
    void deleteUserInvalidUsername() throws Exception {
        Mockito.doThrow(UserNotFoundException.class).when(service).findUserDetails(username);

        assertThrows(UserNotFoundException.class, () -> service.deleteUser(username));
    }

}
