package eventservice.eventservice.business.mapper;

import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapStructImpl.class)
public interface UserMapStruct {

    @Mapping(source="roleDto", target="roleEntity")
    UserEntity dtoToEntity(UserDto userDto);

    @Mapping(source="roleEntity", target="roleDto")
    UserDto entityToDto(UserEntity userEntity);


}
