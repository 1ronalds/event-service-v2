package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.handlers.exceptions.EmailExistsException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.UsernameExistsException;
import eventservice.eventservice.business.mapper.UserMapStruct;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapStruct mapper;

    /**
     * requests from database and returns user details by username
     * @param username
     * @return UserDto
     */
    @Override
    public UserDto findUserDetails(String username){
        Optional<UserEntity> userDetailsEntity = repository.findByUsername(username);
        return userDetailsEntity.map(mapper::entityToDto).orElseThrow(UserNotFoundException::new);
    }

    /**
     * saves in database new user
     * @param user
     * @return UserDto
     */
    @Override
    public UserDto saveUser(UserDto user){
        //Invalid input exceptions are thrown in ExceptionHandlerMethods
        if(repository.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameExistsException();
        } else if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailExistsException();
        } else {
            user.setRole(new RoleDto(2L, "user"));

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            return mapper.entityToDto(repository.save(mapper.dtoToEntity(user)));
        }
    }

    /**
     * saves edits of user information to database
     * @param user
     * @param username
     * @return UserDto
     */
    @Override
    public UserDto editUser(UserDto user, String username){
        //UserNotFound exception is thrown by findUserDetails()
        //Invalid input exceptions are thrown in ExceptionHandlerMethods
        RoleDto role = findUserDetails(username).getRole();
        Long id = findUserDetails(username).getId();
        user.setRole(role);
        user.setId(id);
        
        if(user.getPassword() == null){
            user.setPassword(findUserDetails(username).getPassword());
        } else {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        

        return mapper.entityToDto(repository.save(mapper.dtoToEntity(user)));
    }

    /**
     * deletes user from database by username
     * @param username
     */
    @Override
    public void deleteUser(String username){
        //UserNotFound exception is thrown by findUserDetails()
        Long id = findUserDetails(username).getId();
        repository.deleteById(id);
    }

}
