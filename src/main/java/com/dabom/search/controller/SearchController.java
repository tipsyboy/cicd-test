package com.dabom.search.controller;

import com.dabom.common.BaseResponse;
import com.dabom.common.SliceBaseResponse;
import com.dabom.search.model.dto.SearchResponseDto;
import com.dabom.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.dabom.search.constants.SwaggerConstants.SEARCH_VIDEOS_RESPONSE;

@Tag(name = "검색 기능")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;


    @Operation(
            summary = "비디오 검색",
            description = "키워드를 통해 비디오를 검색하고 페이징된 결과를 반환합니다.",
            parameters = {
                    @Parameter(
                            name = "keyword",
                            description = "검색할 키워드 (선택사항)",
                            required = false,
                            schema = @Schema(type = "string"),
                            example = "강의"
                    ),
                    @Parameter(
                            name = "page",
                            description = "페이지 번호 (0부터 시작)",
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "0"),
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            description = "페이지당 항목 수",
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "10"),
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "비디오 검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "비디오 검색 성공 응답",
                                            value = SEARCH_VIDEOS_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 파라미터",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
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
