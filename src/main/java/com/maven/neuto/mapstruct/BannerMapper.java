package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Banner;
import com.maven.neuto.payload.request.banner.BannerCreateDTO;
import com.maven.neuto.payload.request.banner.BannerUpdateDTO;
import com.maven.neuto.payload.response.banner.BannerResponseDTO;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface BannerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "communityId", expression = "java(communityId)")
    @Mapping(target = "bannerCreator", expression = "java(createdBy)")
    @Mapping(target = "bannerCourse", source = "dto.courseId")
    Banner toEntity(BannerCreateDTO dto, Long communityId, Long createdBy, @Context MapperContext context);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBannerFromDto(BannerUpdateDTO request, @MappingTarget Banner entity);

    // Entity -> Response DTO
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "convertInstantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "convertInstantToLocalDateTime")
    BannerResponseDTO toResponseDto(Banner banner);

    @Named("convertInstantToLocalDateTime")
    default LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }

}
