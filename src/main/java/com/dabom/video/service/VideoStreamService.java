package com.dabom.video.service;

import com.dabom.video.model.Video;
import com.dabom.video.model.dto.VideoInfoResponseDto;
import com.dabom.video.repository.VideoRepository;
import com.dabom.video.service.utils.S3UrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamService {

    private final VideoRepository videoRepository;
    private final S3UrlBuilder s3UrlBuilder;

    public VideoInfoResponseDto getVideoInfo(Integer videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("비디오 못찾음"));
        String savedPath = s3UrlBuilder.buildPublicUrl(video.getSavedPath());
        return VideoInfoResponseDto.toDto(video, savedPath);
    }
}
