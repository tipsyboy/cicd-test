package com.dabom.image.util;

import com.dabom.image.exception.ImageException;
import com.dabom.image.exception.ImageExceptionType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ImageUtils {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public static String generateFileName(String originalFilename) {
        String result = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + result;
    }

    private static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public static void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ImageException(ImageExceptionType.FILE_EMPTY);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageException(ImageExceptionType.FILE_TOO_LARGE);
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ImageException(ImageExceptionType.UNSUPPORTED_FILE_TYPE);
        }
    }
}