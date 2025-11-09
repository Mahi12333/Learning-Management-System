package com.maven.neuto.payload.response.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PresignedUrlResponse {
    private String url;
    private String publicId;
    private long size;
    private Long duration;
}
