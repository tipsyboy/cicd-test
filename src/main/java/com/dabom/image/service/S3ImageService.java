package com.dabom.image.service;

import com.dabom.image.exception.ImageException;
import com.dabom.image.exception.ImageExceptionType;
import com.dabom.image.model.dto.ImageUploadResponseDto;
import com.dabom.image.model.entity.Image;
import com.dabom.image.repository.ImageRepository;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dabom.image.util.ImageUtils.generateFileName;
import static com.dabom.image.util.ImageUtils.validateImage;

@Primary
@Service
@RequiredArgsConstructor
public class S3ImageService implements ImageService {

    private final ImageRepository imageRepository;

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String s3BucketName;
    @Value("${spring.cloud.aws.s3.presigned-url-duration}")
    private long presignedUrlDuration;
    @Value("${file.upload.path}")
    private String uploadPath;
    @Value("${file.upload.url}")
    private String fileUrl;

    @PostConstruct
    public void validateProperties() {
        if (StringUtils.isBlank(s3BucketName) || StringUtils.isBlank(uploadPath) || StringUtils.isBlank(fileUrl)) {
            throw new IllegalStateException("S3 버킷, 업로드 경로, 파일 URL이 설정되지 않았습니다.");
        }
        if (presignedUrlDuration <= 0) {
            throw new IllegalStateException("프리사인드 URL 유효 기간은 0보다 커야 합니다.");
        }
    }

    @Transactional
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
        String s3Key = String.join("/", uploadPath, directory, imageName).replaceAll("//+", "/");
        String imageUrlPath = String.join("/", fileUrl, directory, imageName).replaceAll("//+", "/");

        System.out.println(s3Key);

        // 프리사인드 URL 생성 (PUT 요청용)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3BucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r ->
                r.signatureDuration(Duration.ofSeconds(presignedUrlDuration)).putObjectRequest(putObjectRequest));
        URL presignedUrl = presignedRequest.url();

        Image entity = Image.builder()
                .originalName(file.getOriginalFilename())
                .imageName(imageName)
                .imageUrl(imageUrlPath)
                .imagePath(s3Key)
                .fileSize(file.getSize())
                .build();

        imageRepository.save(entity);

        return ImageUploadResponseDto.builder()
                .idx(entity.getIdx())
                .originalName(entity.getOriginalName())
                .imageName(entity.getImageName())
                .imageUrl(presignedUrl.toString())
                .imagePath(entity.getImagePath())
                .build();
    }

    @Override
    @Transactional
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
            Image image = result.get();
            String s3Key = image.getImagePath();

            // 프리사인드 URL 생성 (GET 요청용)
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(s3Key)
                    .build();
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(r ->
                    r.signatureDuration(Duration.ofSeconds(presignedUrlDuration)).getObjectRequest(getObjectRequest));
            URL presignedUrl = presignedRequest.url();
            return presignedUrl.toString();
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
