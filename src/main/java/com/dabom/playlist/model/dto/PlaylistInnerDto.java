package com.dabom.playlist.model.dto;

import com.dabom.playlist.model.entity.Playlist;
import com.dabom.video.model.Video;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PlaylistInnerDto {

    private String playlistTitle;
    private List<VideoDto> videos;

    public static PlaylistInnerDto from(Playlist playlist, List<Video> videoList) {
        List<VideoDto> videoDtos = videoList.stream()
                .map(VideoDto::from)
                .collect(Collectors.toList());

        return PlaylistInnerDto.builder()
                .playlistTitle(playlist.getPlaylistTitle())
                .videos(videoDtos)
                .build();
    }

    @Getter
    @Builder
    public static class VideoDto {
        private Integer idx;
        private String title;
        private String savedPath;
        private double averageScore;

        public static VideoDto from(Video video) {
            return VideoDto.builder()
                    .idx(video.getIdx())
                    .title(video.getTitle())
                    .savedPath(video.getSavedPath())
                    .averageScore(video.getAverageScore())
                    .build();
        }
    }
}