package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Group;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "community", expression = "java(groupContext.getCommunityId())")
//    @Mapping(target = "createdBy", expression = "java(groupContext.getCreatedById())")
    Group toEntity(GroupCreateDTO dto, Long communityId, Long createdById, @Context MapperContext groupContext);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGroupFromDto(GroupUpdateDTO dto, @MappingTarget Group entity);

    // Entity → Response DTO (for returning )
    GroupResponseDTO toResponseDto(Group entity);
}
