package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Role;
import com.maven.neuto.payload.request.user.RoleDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);
    List<RoleDto> toDtoList(List<Role> roles);
    Role toEntity(RoleDto dto);
}
