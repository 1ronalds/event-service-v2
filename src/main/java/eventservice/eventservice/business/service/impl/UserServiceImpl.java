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
import org.springframework.stereotype.Service;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapStruct mapper;

    @Override
    public UserDto findUserDetails(String username){
        log.info("findUserDetails service method called");

        Optional<UserEntity> userDetailsEntity = repository.findByUsername(username);
        return userDetailsEntity.map(mapper::entityToDto).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserDto saveUser(UserDto user){
        log.info("saveUser service method called");

        user.setRole(new RoleDto(2L, "user"));

        //Invalid input exceptions are thrown in ExceptionHandlerMethods
        if(repository.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameExistsException();
        } else if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailExistsException();
        } else {
            return mapper.entityToDto(repository.save(mapper.dtoToEntity(user)));
        }
    }

    @Override
    public UserDto editUser(UserDto user, String username){
        log.info("editUser service method called");

        //UserNotFound exception is thrown by findUserDetails()
        //Invalid input exceptions are thrown in ExceptionHandlerMethods
        RoleDto role = findUserDetails(username).getRole();
        Long id = findUserDetails(username).getId();
        user.setRole(role);
        user.setId(id);

        return mapper.entityToDto(repository.save(mapper.dtoToEntity(user)));
    }

    @Override
    public void deleteUser(String username){
        log.info("deleteUser service method called");

        //UserNotFound exception is thrown by findUserDetails()
        Long id = findUserDetails(username).getId();
        repository.deleteById(id);
    }

}