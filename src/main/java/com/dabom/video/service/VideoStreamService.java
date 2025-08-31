package com.dabom.video.service;

import com.dabom.video.exception.VideoException;
import com.dabom.video.exception.VideoExceptionType;
import com.dabom.video.model.Video;
import com.dabom.video.model.dto.VideoInfoResponseDto;
import com.dabom.video.repository.VideoRepository;
import com.dabom.video.service.utils.S3UrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamService {

    private final VideoRepository videoRepository;
    private final S3UrlBuilder s3UrlBuilder;

    @Transactional // TODO: views 증가 로직을 위함인데, 스트림에 @Transactional이 붙어야 할까?
    public VideoInfoResponseDto getVideoInfo(Integer videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoException(VideoExceptionType.VIDEO_NOT_FOUND));

        video.incrementViews();

        String savedPath = s3UrlBuilder.buildPublicUrl(video.getSavedPath());
        return VideoInfoResponseDto.toDto(video, savedPath);
    }
}
