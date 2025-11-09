package com.maven.neuto.serviceImplement;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.maven.neuto.config.MessageConfig;
import com.maven.neuto.emun.UploadSectionName;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.model.UploadedFile;
import com.maven.neuto.payload.response.file.PresignedUrlResponse;
import com.maven.neuto.repository.UploadedFileRepository;
import com.maven.neuto.service.AssetFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetFileServiceImple implements AssetFileService {
    private final Cloudinary cloudinary;
    private final MessageConfig messageConfig;
    private final UploadedFileRepository uploadedFileRepository;

    @Override
    public PresignedUrlResponse uploadToTemp(MultipartFile file, String name) throws IOException {
        log.info("Uploading file to Cloudinary: {} {}", file, name);
        UploadSectionName fileType;
        try {
            fileType = UploadSectionName.valueOf(name.toUpperCase()); // convert string to enum
        } catch (IllegalArgumentException e) {
            throw new APIException("file.profile.invalid", HttpStatus.BAD_REQUEST);
        }

        validateFile(file, fileType);
        // Original filename (without extension)
        String originalName = FilenameUtils.getBaseName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        // Generate timestamp
        long timestamp = System.currentTimeMillis();
        String folderName = fileType.name().toLowerCase();


        // Public ID with timestamp suffix
        String publicId = folderName + "_" + timestamp + "." + extension;

        // Upload to Cloudinary with custom publicId
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "auto",
                        "overwrite", true
                ));

        String url = uploadResult.get("secure_url").toString();
        String savedPublicId = uploadResult.get("public_id").toString();
        long size = file.getSize();


        Long duration = null;
        if (name.equalsIgnoreCase(UploadSectionName.CONTENT.toString())) { // Only for videos
            duration = uploadResult.containsKey("duration")
                    ? ((Number) uploadResult.get("duration")).longValue()
                    : null;
        } else if (name.equalsIgnoreCase(UploadSectionName.LESSON.toString())) {
            duration = uploadResult.containsKey("duration")
                    ? ((Number) uploadResult.get("duration")).longValue()
                    : null;
        }

        // ✅ Save metadata to DB
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setUrl(url);
        uploadedFile.setPublicId(savedPublicId);
        uploadedFile.setSize(size);
        uploadedFile.setDuration(duration);
        uploadedFile.setSectionName(fileType);
        uploadedFileRepository.save(uploadedFile);

        return new PresignedUrlResponse(url, savedPublicId, size, duration);
    }

    @Override
    public List<PresignedUrlResponse> uploadMultiple(List<MultipartFile> files, String name) throws IOException {
        UploadSectionName fileType;
        try {
            fileType = UploadSectionName.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new APIException("file.invalid.type", HttpStatus.BAD_REQUEST);
        }

        List<PresignedUrlResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            validateFile(file, fileType);

            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            long timestamp = System.currentTimeMillis();
            String folderName = fileType.name().toLowerCase();
            String publicId = folderName + "_" + timestamp + "." + extension;

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", publicId, "resource_type", "auto", "overwrite", true));

            String url = uploadResult.get("secure_url").toString();
            String savedPublicId = uploadResult.get("public_id").toString();
            long size = file.getSize();

            Long duration = null;
            if ("unit".equalsIgnoreCase(name) && uploadResult.containsKey("duration")) {
                duration = ((Number) uploadResult.get("duration")).longValue();
            }

            // Save in DB
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setUrl(url);
            uploadedFile.setPublicId(savedPublicId);
            uploadedFile.setSize(size);
            uploadedFile.setDuration(duration);
            uploadedFile.setSectionName(fileType);
            uploadedFileRepository.save(uploadedFile);

            responses.add(new PresignedUrlResponse(url, savedPublicId, size, duration));
        }

        return responses;
    }

    private void validateFile(MultipartFile file, UploadSectionName name) {
        String contentType = file.getContentType().toLowerCase();
        long fileSize = file.getSize();
        log.info("Content Type: {} {} ", contentType, fileSize);
        switch (name) {
            case PROFILE: // only images max 10MB
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new APIException("file.profile.invalid", HttpStatus.BAD_REQUEST);
                }
                if (fileSize > 10 * 1024 * 1024) {
                    throw new APIException("file.profile.size", HttpStatus.BAD_REQUEST);
                }
                break;

            case COMMUNITY: // only images max 10MB
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new APIException("file.profile.invalid", HttpStatus.BAD_REQUEST);
                }
                if (fileSize > 10 * 1024 * 1024) {
                    throw new APIException("file.profile.size", HttpStatus.BAD_REQUEST);
                }
                break;

            case COURSE: // only videos max 50MB
                if (contentType == null || !contentType.startsWith("video/")) {
                    throw new APIException("file.course.invalid", HttpStatus.BAD_REQUEST);
                }
                if (fileSize > 50 * 1024 * 1024) {
                    throw new APIException("file.course.size", HttpStatus.BAD_REQUEST);
                }
                break;

            case LESSON: // Image (<=10MB) and Video (<=50MB)
                if (contentType == null ||
                        (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                    throw new APIException("file.lesson.invalid", HttpStatus.BAD_REQUEST);
                }
                if (contentType.startsWith("image/") && fileSize > 10 * 1024 * 1024) {
                    throw new APIException("file.lesson.image.size", HttpStatus.BAD_REQUEST);
                }
                if (contentType.startsWith("video/") && fileSize > 50 * 1024 * 1024) {
                    throw new APIException("file.lesson.video.size", HttpStatus.BAD_REQUEST);
                }
                break;

            case CONTENT: // Only video uploads allowed
                if (contentType == null || !contentType.startsWith("video/")) {
                    throw new APIException("file.lesson.invalid", HttpStatus.BAD_REQUEST);
                }

                long maxVideoSize = 50L * 1024 * 1024; // 50 MB
                if (fileSize > maxVideoSize) {
                    throw new APIException("file.lesson.video.size", HttpStatus.BAD_REQUEST);
                }

                break;

//            case INVAITED: // only CSV max 8MB
//                if (contentType == null ||
//                        !(contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel"))) {
//                    throw new IllegalArgumentException("Only CSV files are allowed for invited");
//                }
//                if (fileSize > 8 * 1024 * 1024) {
//                    throw new IllegalArgumentException("CSV file size must be <= 8MB");
//                }
//                break;

            default:
                throw new APIException("file.unsupported", HttpStatus.BAD_REQUEST);
        }
    }

    public String moveToPermanent(String tempUrl, String permanentFolder) throws IOException {
        String tempPublicId  = extractPublicId(tempUrl);
        // Generate new publicId with permanent folder + timestamp
        String fileName = tempPublicId.substring(tempPublicId.lastIndexOf("/") + 1);

        // Create new publicId with permanent folder (same filename, so timestamp preserved)
        String newPublicId = permanentFolder + "/" + fileName;

        // Copy asset in Cloudinary
        cloudinary.uploader().rename(tempPublicId, newPublicId, ObjectUtils.asMap(
                "overwrite", true,
                "resource_type", "auto"
        ));

        // Return permanent URL
        return cloudinary.url()
                .resourceType("auto")
                .generate(newPublicId);
    }

    private String extractPublicId(String tempUrl) {
        String withoutVersion = tempUrl.substring(tempUrl.indexOf("/upload/") + 8);
        return withoutVersion.substring(0, withoutVersion.lastIndexOf("."));     }

}
