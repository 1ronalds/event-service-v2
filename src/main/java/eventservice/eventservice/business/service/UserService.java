package eventservice.eventservice.business.service;

import eventservice.eventservice.model.UserDto;

public interface UserService {
    UserDto findUserDetails(String username);
}
