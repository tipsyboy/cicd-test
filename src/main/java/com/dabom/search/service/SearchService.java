package com.dabom.search.service;

import com.dabom.common.SliceBaseResponse;
import com.dabom.search.model.dto.SearchResponseDto;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final VideoRepository videoRepository;

    public SliceBaseResponse<SearchResponseDto> getVideos(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Video> videoSlice;

        if (keyword == null || keyword.trim().isEmpty()) {
            videoSlice = videoRepository.findVisibleVideosWithFetchJoin(pageable);
        } else {
            videoSlice = videoRepository.searchByKeywordWithFetchJoin(keyword.trim(), pageable);
        }

        List<SearchResponseDto> result = videoSlice.stream()
                .map(SearchResponseDto::from)
                .toList();
        return new SliceBaseResponse<>(result, videoSlice.hasNext());
    }
}