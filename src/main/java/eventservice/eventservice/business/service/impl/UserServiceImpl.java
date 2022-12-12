package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.handlers.exceptions.EmailExistsException;
import eventservice.eventservice.business.handlers.exceptions.UserNotFoundException;
import eventservice.eventservice.business.handlers.exceptions.UsernameExistsException;
import eventservice.eventservice.business.mapper.UserMapStruct;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapStruct mapper;

    @Override
    public UserDto findUserDetails(String username){
        Optional<UserEntity> userDetailsEntity = repository.findByUsername(username);
        return userDetailsEntity.map(mapper::entityToDto).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserDto saveUser(UserDto user){
        if(repository.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameExistsException();
        } else if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailExistsException();
        } else {
            return mapper.entityToDto(repository.save(mapper.dtoToEntity(user)));
        }
    }

    @Override
    public UserDto editUser(UserDto user){
        if(repository.findByUsername(user.getUsername()).isPresent()){
            user.setId(repository.findByUsername(user.getUsername()).get().getId());
            return mapper.entityToDto(repository.save(mapper.dtoToEntity(user)));
        } else {
            throw new UserNotFoundException();
        }

    }

}
