package eventservice.eventservice.business.mapper;

import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.model.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapStruct {

    RoleDto entityToDto(RoleEntity roleEntity);

    RoleEntity dtoToEntity(RoleDto roleDto);
}
