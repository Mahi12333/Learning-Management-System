package com.maven.neuto.mapstruct;


import com.maven.neuto.model.ShortContentUpload;
import com.maven.neuto.payload.request.content.ShortContentCreateDTO;
import com.maven.neuto.payload.request.content.ShortContentUpdatedDTO;
import com.maven.neuto.payload.response.content.ShortContentResponseDTO;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Mapper(componentModel = "spring")
public interface ShortContentMapper {

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "shortContentCommunity", ignore = true)
    ShortContentUpload toCreateShortContentEntity(ShortContentCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateShortContentFromDto(ShortContentUpdatedDTO dto, @MappingTarget ShortContentUpload entity);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "convertInstantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "convertInstantToLocalDateTime")
    ShortContentResponseDTO toShortContentResponseDto(ShortContentUpload entity);

    @Named("convertInstantToLocalDateTime")
    default LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }

}
