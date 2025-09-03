package com.dabom.image.service;

import com.dabom.image.exception.ImageException;
import com.dabom.image.exception.ImageExceptionType;
import com.dabom.image.model.dto.ImageUploadResponseDto;
import com.dabom.image.model.entity.Image;
import com.dabom.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dabom.image.util.ImageUtils.*;

@Service
@Transactional
@RequiredArgsConstructor
public class LocalImageService implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url}")
    private String fileUrl;

    @Override
    public ImageUploadResponseDto uploadSingleImage(MultipartFile file, String directory) throws ImageException {

        try {
            validateImage(file);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("비어있습니다")) {
                throw new ImageException(ImageExceptionType.FILE_EMPTY);
            } else if (e.getMessage().contains("초과할 수 없습니다")) {
                throw new ImageException(ImageExceptionType.FILE_TOO_LARGE);
            } else if (e.getMessage().contains("지원하지 않는 파일 형식")) {
                throw new ImageException(ImageExceptionType.UNSUPPORTED_FILE_TYPE);
            } else {
                throw new ImageException(ImageExceptionType.UPLOAD_FAILED);
            }
        }

        String imageName = generateFileName(file.getOriginalFilename());
        String imagePath = uploadPath + "/" + directory + "/" + imageName;
        String imageUrlPath = fileUrl + "/" + directory + "/" + imageName;

        try {
            createDirectoryIfNotExists(uploadPath + "/" + directory);
            file.transferTo(new File(imagePath));

            Image entity = Image.builder()
                    .originalName(file.getOriginalFilename())
                    .imageName(imageName)
                    .imageUrl(imageUrlPath)
                    .imagePath(imagePath)
                    .fileSize(file.getSize())
                    .build();

            imageRepository.save(entity);

            return ImageUploadResponseDto.from(entity);

        } catch (IOException e) {
            throw new ImageException(ImageExceptionType.UPLOAD_FAILED);
        }
    }

    @Override
    public List<ImageUploadResponseDto> uploadMultipleImages(List<MultipartFile> files, String directory) throws ImageException {
        return files.stream()
                .map(file -> {
                    try {
                        return uploadSingleImage(file, directory);
                    } catch (ImageException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new ImageException(ImageExceptionType.UPLOAD_FAILED);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public String find(Integer idx) throws ImageException {
        Optional<Image> result = imageRepository.findById(idx);
        if (result.isPresent()) {
            return result.get().getImageUrl();
        } else {
            throw new ImageException(ImageExceptionType.IMAGE_NOT_FOUND);
        }
    }

    @Override
    public void deleteImage(Integer idx) throws ImageException {
        Optional<Image> result = imageRepository.findById(idx);
        if (result.isPresent()) {
            Image delImg = result.get();
            delImg.safeDelete();
        } else {
            throw new ImageException(ImageExceptionType.IMAGE_NOT_FOUND);
        }
    }
}
