package com.dabom.video.service;

import com.dabom.video.model.Video;
import com.dabom.video.model.dto.VideoMetadataRequestDto;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    @Transactional
    public Integer mappingMetadata(VideoMetadataRequestDto requestDto) {
        Video video = videoRepository.findById(requestDto.getIdx())
                .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다. idx=" + requestDto.getIdx()));

        video.mappingVideoMetadata(requestDto.getTitle(), requestDto.getDescription(), requestDto.isPublic());

        // TODO: 리턴 뭐해야되지
        return 1;
    }
}
