package com.maven.neuto.controller;


import com.maven.neuto.payload.response.file.PresignedUrlResponse;
import com.maven.neuto.service.AssetFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class AssetFileController {
    private final AssetFileService assetFileService;

    @PostMapping("/file-upload")
    public ResponseEntity<PresignedUrlResponse> uploadTemp(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws Exception {
        PresignedUrlResponse tempUrl = assetFileService.uploadToTemp(file, name);
        return ResponseEntity.ok(tempUrl);
    }
    /*public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(@RequestBody @Valid  PresignedUrlRequest request) {
        PresignedUrlResponse response = assetFileService.saveFileToBucket(request);
        return ResponseEntity.ok(response);
    }*/

    @PostMapping("/multiple")
    public ResponseEntity<List<PresignedUrlResponse>> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("name") String name) throws IOException {
        return ResponseEntity.ok(assetFileService.uploadMultiple(files, name));
    }
}
