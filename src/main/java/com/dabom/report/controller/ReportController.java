package com.dabom.report.controller;

import com.dabom.common.BaseResponse;
import com.dabom.member.security.dto.MemberDetailsDto;
import com.dabom.report.constansts.SwaggerConstants;
import com.dabom.report.model.dto.ReportRegisterDto;
import com.dabom.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// MemberDetailsDto의 실제 경로에 따라 import 문이 달라질 수 있습니다.

@Tag(name = "신고 기능", description = "콘텐츠 신고 관련 API")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "콘텐츠 신고", description = "비디오, 댓글 등 다양한 콘텐츠를 신고합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 접수 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = SwaggerConstants.REPORT_REGISTER_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "신고 정보",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReportRegisterDto.class),
                            examples = @ExampleObject(value = SwaggerConstants.REPORT_REGISTER_REQUEST)
                    )
            )
            @RequestBody ReportRegisterDto<Integer> reportDto,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        Integer reporterMemberIdx = memberDetailsDto.getIdx();

        try {
            reportService.registerReport(reportDto, reporterMemberIdx);
            return new ResponseEntity<>("Report registered successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred during report registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}