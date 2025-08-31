package com.dabom.video.repository;

import com.dabom.video.model.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends JpaRepository<Video, Integer>,VideoRepositoryCustom  {

    // 키워드 검색말고 그냥 비디오 전체검색임
    @Query(value = "SELECT * FROM video WHERE is_public = 1 ORDER BY created_at DESC", nativeQuery = true)
    Slice<Video> findVisibleVideosOrderByCreatedAtDesc(Pageable pageable);

    // 나중에 VideoStatus videoStatus 매게변수에 이거넣으면 좀더 완벽쓰


    // 특정 키워드 검색 쿼리(관련도 높은 순)
    @Query(value = "SELECT * FROM video WHERE is_public  = 1 " +
                   "AND MATCH(title, description) AGAINST (:keyword IN BOOLEAN MODE) " +
                   "ORDER BY MATCH(title, description) AGAINST (:keyword IN BOOLEAN MODE) DESC, created_at DESC",
            nativeQuery = true)
    Slice<Video> searchByKeywordOrderByRelevance(@Param("keyword") String keyword, Pageable pageable);
    //"AND v.video_status = 'DONE' " +  // 인코딩 완료된 것만
    //  나중에 이거 추가하면 완벽쓰
}
