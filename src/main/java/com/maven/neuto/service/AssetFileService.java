package com.maven.neuto.service;

import com.maven.neuto.payload.request.file.PresignedUrlRequest;
import com.maven.neuto.payload.response.file.PresignedUrlResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public interface AssetFileService {
    PresignedUrlResponse uploadToTemp(MultipartFile file, String name) throws IOException;
    List<PresignedUrlResponse> uploadMultiple(List<MultipartFile> files, String name) throws IOException;
}
