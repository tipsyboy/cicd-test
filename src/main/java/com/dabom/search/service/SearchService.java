package com.dabom.search.service;

import com.dabom.common.SliceBaseResponse;
import com.dabom.member.service.MemberService;
import com.dabom.s3.S3UrlBuilder;
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
    private final MemberService memberService;
    private final S3UrlBuilder s3UrlBuilder;

    private static final String DEFAULT_PROFILE_IMAGE = "/Image/Dabompng.png";
    private static final String DEFAULT_THUMBNAIL_IMAGE = "/Image/Dabompng.png";

    public SliceBaseResponse<SearchResponseDto> getVideos(String keyword, String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Video> videoSlice;

        if (name != null && !name.trim().isEmpty()) {
            videoSlice = videoRepository.searchByNameWithFetchJoin(name.trim(), pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            videoSlice = videoRepository.searchByKeywordWithFetchJoin(keyword.trim(), pageable);
        } else {
            videoSlice = videoRepository.findVisibleVideosWithFetchJoin(pageable);
        }

        List<SearchResponseDto> result = videoSlice.stream()
                .map(video -> {
                    String profileImg = getProfileImgSafely(video);
                    String thumbnail = getThumbnailSafely(video);
                    return SearchResponseDto.from(video, profileImg, thumbnail);
                })
                .toList();

        return new SliceBaseResponse<>(result, videoSlice.hasNext());
    }

    private String getProfileImgSafely(Video video) {
        try {
            if (video.getChannel() != null) {
                String profileImg = memberService.getProfileImg(video.getChannel().getIdx());
                if (profileImg != null && !profileImg.trim().isEmpty()) {
                    return profileImg;
                }
            }
        } catch (Exception e) {
            log.warn("프로필 이미지 조회 실패");
        }
        return DEFAULT_PROFILE_IMAGE;
    }

    private String getThumbnailSafely(Video video) {
        try {
            if (video.getThumbnailImage() != null &&
                video.getThumbnailImage().getSavedPath() != null) {

                String savedPath = video.getThumbnailImage().getSavedPath();
                if (!savedPath.trim().isEmpty()) {
                    return s3UrlBuilder.buildPublicUrl(savedPath);
                }
            }
        } catch (Exception e) {
            log.warn("썸네일 이미지 조회 실패 ");
        }
        return DEFAULT_THUMBNAIL_IMAGE;
    }
}