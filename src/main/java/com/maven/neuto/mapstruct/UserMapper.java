package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Community;
import com.maven.neuto.model.Role;
import com.maven.neuto.model.User;
import com.maven.neuto.payload.request.user.AdminInviteDTO;
import com.maven.neuto.payload.request.user.InviteDTO;
import com.maven.neuto.payload.request.user.UserDto;
import com.maven.neuto.payload.response.user.CommunityLoginResgistrationDTO;
import com.maven.neuto.payload.response.user.ProfileVisitDTO;
import com.maven.neuto.payload.response.user.SuperAdminProfileResponseDTO;
import com.maven.neuto.payload.response.user.UserDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "status", target = "status")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "role", ignore = true) // handled manually
    @Mapping(target = "password", ignore = true) // don’t map password from DTO
    User toEntity(UserDto dto);


    @Mapping(source = "role.id", target = "roleId", qualifiedByName = "roleIdToString")
    @Mapping(source = "profilePicture", target = "profileImage")
    UserDetailsDTO toUserDetailsDTO(User user);

    CommunityLoginResgistrationDTO toCommunityLoginResgistrationDTOs(Community communities);

    @Mapping(target = "role", expression = "java(roleAdmin())")
    @Mapping(target = "status", expression = "java(Status.INACTIVE)")
    @Mapping(target = "email", expression = "java(dto.getEmail().trim().toLowerCase())")
    @Mapping(target= "userName", ignore = true)
    User toEntityAdminInvited(AdminInviteDTO dto);

    default Role roleAdmin() {
        return new Role(2L, "Admin"); // or just new Role(2L) if single-arg constructor exists
    }

    @Mapping(source = "role.id", target = "roleId", qualifiedByName = "roleIdToString")
    @Mapping(source = "profilePicture", target = "profileImage")
    UserDetailsDTO toProfileCompleteDTO(User user);

    @Named("roleIdToString")
    default String roleIdToString(Integer roleId) {
        return roleId != null ? String.valueOf(roleId) : null;
    }

    @Mapping(source = "following", target = "isFollowing", qualifiedByName = "convertBoolean")
    @Mapping(source = "followers", target = "isFollower", qualifiedByName = "convertBoolean")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "convertInstantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "convertInstantToLocalDateTime")
    @Mapping(source = "role.id", target = "roleId")
//    @Mapping(target = "access", ignore = true)
    ProfileVisitDTO toUserProfileVisitDTO(User user);

    SuperAdminProfileResponseDTO toSuperAminProfileDTO(User user);


    @Named("convertInstantToLocalDateTime")
    default LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }

    @Named("convertBoolean")
    default boolean convertBoolean(List<?> list) {
        return list != null && !list.isEmpty();
    }


}
