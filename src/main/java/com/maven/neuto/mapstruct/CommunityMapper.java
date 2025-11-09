package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Community;
import com.maven.neuto.payload.request.community.CreateCommunityDTO;
import com.maven.neuto.payload.request.community.UpdatedCommunityDTO;
import com.maven.neuto.payload.response.community.CommunityResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(target = "communityCreatedUser", ignore = true)
    @Mapping(target = "industry", ignore = true)
    @Mapping(target = "tagline", source = "tagLine")
    Community toEntityCreateCommunity(CreateCommunityDTO dto);

    // Map Community → CommunityResponseDTO
    @Mapping(target = "communityCreatedUser", source = "communityCreatedUser.id")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "convertInstantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "convertInstantToLocalDateTime")
    CommunityResponseDTO toDtoResponseCommunity(Community community);

    @Mapping(target = "communityCreatedUser", ignore = true)
    @Mapping(target = "industry", ignore = true)
    void updateCommunityFromDto(UpdatedCommunityDTO dto, @MappingTarget Community entity);

    @Named("convertInstantToLocalDateTime")
    default LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }

}
