package com.dabom.video.repository;

import com.dabom.video.model.Video;
import com.dabom.video.model.VideoTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.dabom.video.model.QVideo.video;
import static com.dabom.member.model.entity.QMember.member;

@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Video> searchByKeywordWithFetchJoin(String keyword, Pageable pageable) {
        List<Video> videos = queryFactory
                .selectFrom(video)
                .join(video.channel, member).fetchJoin()
                .where(
                        video.isPublic.eq(true)
                                .and(
                                        video.title.containsIgnoreCase(keyword)
                                                .or(video.description.containsIgnoreCase(keyword))
                                                .or(videoTagContains(keyword))
                                )
                )
                .orderBy(video.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return createSlice(videos, pageable);
    }

    @Override
    public Slice<Video> findVisibleVideosWithFetchJoin(Pageable pageable) {
        List<Video> videos = queryFactory
                .selectFrom(video)
                .join(video.channel, member).fetchJoin()
                .where(video.isPublic.eq(true))
                .orderBy(video.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return createSlice(videos, pageable);
    }

    private BooleanExpression videoTagContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        List<VideoTag> matchingTags = Arrays.stream(VideoTag.values())
                .filter(tag -> tag.getDisplayName().contains(keyword))
                .collect(Collectors.toList());

        if (matchingTags.isEmpty()) {
            return null;
        }

        return video.videoTag.in(matchingTags);
    }

    private Slice<Video> createSlice(List<Video> videos, Pageable pageable) {
        boolean hasNext = videos.size() > pageable.getPageSize();

        if (hasNext) {
            videos.remove(videos.size() - 1);
        }

        return new SliceImpl<>(videos, pageable, hasNext);
    }
}