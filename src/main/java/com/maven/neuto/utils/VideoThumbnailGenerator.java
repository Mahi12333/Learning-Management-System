package com.maven.neuto.utils;

import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;


@Configuration
public class VideoThumbnailGenerator {

    public static File generateVideoThumbnail(String videoUrl) throws IOException {
        // 1. Download video from URL into temp file
        File tempVideo = File.createTempFile("video_", ".mp4");
        try (InputStream in = new URL(videoUrl).openStream();
             FileOutputStream out = new FileOutputStream(tempVideo)) {
            in.transferTo(out);
        }
        File outputDir = new File("thumbnails");
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // create directory if not exists
        }
        // 2. Prepare permanent thumbnail file path
        long timestamp = System.currentTimeMillis();
        File thumbnailFile = new File(outputDir, "thumbnail_" + timestamp + ".png");

        // 3. Run FFmpeg command to capture thumbnail at 3rd second
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",                        // overwrite if exists
                "-i", tempVideo.getAbsolutePath(),
                "-ss", "00:00:03",           // capture at 3rd second
                "-vframes", "1",             // 1 frame only
                thumbnailFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Log FFmpeg output (optional)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thumbnail generation interrupted", e);
        }

        // 4. Delete temp video after thumbnail generation
        Files.deleteIfExists(tempVideo.toPath());

        return thumbnailFile;
    }
}
