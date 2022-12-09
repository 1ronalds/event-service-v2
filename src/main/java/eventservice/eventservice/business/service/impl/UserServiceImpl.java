package eventservice.eventservice.business.service.impl;

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
        return userDetailsEntity.map(mapper::entityToDto).orElseThrow(() -> new RuntimeException("NEIN!"));
    }
}
