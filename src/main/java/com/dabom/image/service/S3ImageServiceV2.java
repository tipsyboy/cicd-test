package com.dabom.image.service;

import com.dabom.image.model.dto.ImageCreateRequestDto;
import com.dabom.member.exception.MemberException;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.s3.PresignedUrlRequestDto;
import com.dabom.s3.S3FileManager;
import com.dabom.s3.S3PresignedUrlInformationDto;
import com.dabom.image.model.dto.PresignedUrlResponseDto;
import com.dabom.image.model.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dabom.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class S3ImageServiceV2 {

    private static final String PROFILE_IMAGE_FILE_PATH = "images/profile";
    private static final String BANNER_IMAGE_FILE_PATH = "images/banner";
    private static final String VIDEO_THUMBNAIL_IMAGE_FILE_PATH = "images/thumbnail";

    private final MemberRepository memberRepository;
    private final S3FileManager s3FileManager;


    public PresignedUrlResponseDto getProfileImagePresignedUrl(PresignedUrlRequestDto requestDto) {
        return createPresignedUrl(requestDto, PROFILE_IMAGE_FILE_PATH);
    }

    public PresignedUrlResponseDto getBannerImagePresignedUrl(PresignedUrlRequestDto requestDto) {
        return createPresignedUrl(requestDto, BANNER_IMAGE_FILE_PATH);
    }

    public PresignedUrlResponseDto getThumbnailPresignedUrl(PresignedUrlRequestDto requestDto) {
        return createPresignedUrl(requestDto, VIDEO_THUMBNAIL_IMAGE_FILE_PATH);
    }

    @Transactional
    public Integer createImageEntity(ImageCreateRequestDto requestDto, Integer memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Image image = Image.builder()
                .originalFilename(requestDto.originalFilename())
                .savedPath(requestDto.s3Key())
                .fileSize(requestDto.fileSize())
                .contentType(requestDto.contentType())
                .imageType(requestDto.imageType())
                .build();

        switch (requestDto.imageType()) {
            case PROFILE -> member.updateProfileImage(image);
            case BANNER -> member.updateBannerImage(image);
        }

        return member.getProfileImage().getIdx();
    }


    private PresignedUrlResponseDto createPresignedUrl(PresignedUrlRequestDto requestDto, String directoryPath) {
        String s3Key = s3FileManager.generateS3Key(requestDto.originalFilename(), directoryPath);

        S3PresignedUrlInformationDto presignedUrl =
                s3FileManager.createPresignedUrl(s3Key, requestDto.contentType());

        return PresignedUrlResponseDto.builder()
                .uploadUrl(presignedUrl.uploadUrl())
                .s3Key(s3Key)
                .expiresIn(presignedUrl.expiresIn())
                .build();
    }
}
