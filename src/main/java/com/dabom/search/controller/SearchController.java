package com.dabom.search.controller;

import com.dabom.common.BaseResponse;
import com.dabom.common.SliceBaseResponse;
import com.dabom.search.model.dto.SearchResponseDto;
import com.dabom.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/videos")
    public ResponseEntity<BaseResponse<SliceBaseResponse<SearchResponseDto>>> getVideos(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        SliceBaseResponse<SearchResponseDto> result = searchService.getVideos(keyword, page, size);

        return ResponseEntity.ok(
                BaseResponse.of(result, HttpStatus.OK, "비디오 조회 완료"));
    }

}
