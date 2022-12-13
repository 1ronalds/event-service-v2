package eventservice.eventservice.controller;

import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import eventservice.eventservice.web.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

public class UserControllerUnitTest {
    @Mock
    UserService service;

    @InjectMocks
    UserController controller;

    UserDto userDto;
    String username;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        RoleDto roleDto = new RoleDto(1L, "admin");
        userDto = new UserDto(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleDto);
        username = "AdminUser";
    }

    @Test
    void findUserDetails() throws Exception {
        Mockito.when(service.findUserDetails(username)).thenReturn(userDto);

        ResponseEntity<UserDto> response = controller.findUserDetails(username);
        assertEquals(ResponseEntity.ok(userDto), response);
    }

    @Test
    void saveUser() throws Exception {
        Mockito.when(service.saveUser(userDto)).thenReturn(userDto);

        ResponseEntity<UserDto> response = controller.saveUser(userDto);
        Mockito.verify(service, times(1)).saveUser(userDto);
    }

    @Test
    void editUser() throws Exception {
        Mockito.when(service.editUser(userDto, username)).thenReturn(userDto);

        ResponseEntity<UserDto> response = controller.editUser(username, userDto);
        Mockito.verify(service, times(1)).editUser(userDto, username);
    }

    @Test
    void deleteUser() throws Exception {
        controller.deleteUser(username);
        Mockito.verify(service, times(1)).deleteUser(username);
    }

}