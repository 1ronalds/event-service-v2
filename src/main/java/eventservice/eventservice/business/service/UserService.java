package eventservice.eventservice.business.service;

import eventservice.eventservice.model.UserDto;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface UserService {

    UserDto findUserDetails(String username);
    UserDto saveUser(UserDto user);
    UserDto editUser(UserDto user, String username);
    void deleteUser(String username);
}
