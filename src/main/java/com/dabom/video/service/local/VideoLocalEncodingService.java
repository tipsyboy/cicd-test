package com.dabom.video.service.local;

import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import com.dabom.video.service.utils.FfmpegEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoLocalEncodingService {

    private final VideoRepository videoRepository;
    private final FfmpegEncoder ffmpegEncoder;

    @Async("ffmpegExecutor")
    public void encode(Integer videoIdx) throws IOException {
        Video video = videoRepository.findById(videoIdx)
                .orElseThrow();

        String encodedPath = ffmpegEncoder.encode(video.getOriginalPath());
        String webPath = encodedPath + "/" + "index.m3u8";
        String finalWebPath = webPath.replaceFirst("^videos/", "/hls/");

        video.updateSavedPath(finalWebPath);
        log.info("saved path={}", finalWebPath);
        videoRepository.save(video); // TODO: Manager
    }
}