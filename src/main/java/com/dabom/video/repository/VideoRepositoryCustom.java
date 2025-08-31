package com.dabom.video.repository;

import com.dabom.video.model.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface VideoRepositoryCustom {

    Slice<Video> searchByKeywordWithFetchJoin(String keyword, Pageable pageable);

    Slice<Video> findVisibleVideosWithFetchJoin(Pageable pageable);
}