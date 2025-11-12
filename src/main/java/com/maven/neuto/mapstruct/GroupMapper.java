package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Group;
import com.maven.neuto.model.GroupComment;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.group.GroupCommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "community.id", source = "communityId")
    @Mapping(target = "createdBy.id", source = "createdById")
    Group toEntity(GroupCreateDTO dto, Long communityId, Long createdById, @Context MapperContext groupContext);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGroupFromDto(GroupUpdateDTO dto, @MappingTarget Group entity);

    // Entity → Response DTO (for returning )
    GroupResponseDTO toResponseDto(Group entity);


    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "user.id", target = "user")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "replyToComment.id", target = "replyToComment")
    @Mapping(source = "replyToUser.id", target = "replyToUser")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "convertInstantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "convertInstantToLocalDateTime")
    GroupCommentResponseDTO toGroupCommentResponseDTO(GroupComment saved);

    @Named("convertInstantToLocalDateTime")
    default LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }
}
