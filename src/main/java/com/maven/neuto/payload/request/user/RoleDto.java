package com.maven.neuto.payload.request.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RoleDto {
    private Long id;
    private String name;
}
