package com.dabom.image.service;

import com.dabom.image.model.dto.ImageCreateRequestDto;
import com.dabom.member.exception.MemberException;
import com.dabom.member.exception.MemberExceptionType;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.s3.PresignedUrlRequestDto;
import com.dabom.s3.S3FileManager;
import com.dabom.s3.S3PresignedUrlInformationDto;
import com.dabom.image.exception.ImageException;
import com.dabom.image.model.dto.ImagePresignedUrlResponseDto;
import com.dabom.image.model.entity.Image;
import com.dabom.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class S3ImageServiceV2 {

    private static final String IMAGE_FILE_PATH = "images/";

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final S3FileManager s3FileManager;


    @Transactional
    public ImagePresignedUrlResponseDto getPresigned(PresignedUrlRequestDto requestDto) throws ImageException {

        String s3Key = s3FileManager.generateS3Key(requestDto.originalFilename(), IMAGE_FILE_PATH);

//        Integer imageIdx = createImageEntity(file, s3Key);

        S3PresignedUrlInformationDto presignedUrl = s3FileManager.createPresignedUrl(s3Key, requestDto.contentType());

        return ImagePresignedUrlResponseDto.builder()
                .uploadUrl(presignedUrl.uploadUrl())
                .s3Key(s3Key)
                .expiresIn(presignedUrl.expiresIn())
                .build();
    }

    @Transactional
    public Integer createImage(ImageCreateRequestDto requestDto, Integer memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Image image = Image.builder()
                .originalFilename(requestDto.originalFilename())
                .savedPath(requestDto.s3Key())
                .fileSize(requestDto.fileSize())
                .contentType(requestDto.contentType())
                .imageType(requestDto.imageType())
                .build();
        member.updateProfileImage(image);

        return imageRepository.save(image).getIdx();
    }
}
