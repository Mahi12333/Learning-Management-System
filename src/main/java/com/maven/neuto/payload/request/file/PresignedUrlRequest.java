package com.maven.neuto.payload.request.file;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PresignedUrlRequest {
    private String contentType;
    private long fileSize;
}
