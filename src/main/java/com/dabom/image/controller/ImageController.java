package com.dabom.image.controller;

import com.dabom.common.BaseResponse;
import com.dabom.image.constants.ImageSwaggerConstants;
import com.dabom.image.model.dto.ImageUploadResponseDto;
import com.dabom.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "이미지 관리 기능")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "단일 이미지 업로드",
            description = "지정한 디렉토리에 단일 이미지를 업로드합니다.",
            requestBody = @RequestBody(
                    description = "업로드할 이미지 파일과 디렉토리",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = MultipartFile.class),
                            examples = @ExampleObject(
                                    name = "이미지 업로드 요청 예시",
                                    value = ImageSwaggerConstants.IMAGE_UPLOAD_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이미지 업로드 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ImageUploadResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "이미지 업로드 성공 응답",
                                            value = ImageSwaggerConstants.IMAGE_UPLOAD_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 파일 형식 또는 크기 초과",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/upload")
    public ResponseEntity<BaseResponse<ImageUploadResponseDto>> register(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory) throws IOException {
        ImageUploadResponseDto response = imageService.uploadSingleImage(file, directory);
        System.out.println(response);
        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK, "이미지 업로드 성공"));
    }

    @Operation(
            summary = "다중 이미지 업로드",
            description = "지정한 디렉토리에 여러 이미지를 업로드합니다.",
            requestBody = @RequestBody(
                    description = "업로드할 이미지 파일 목록과 디렉토리",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = MultipartFile.class),
                            examples = @ExampleObject(
                                    name = "다중 이미지 업로드 요청 예시",
                                    value = ImageSwaggerConstants.IMAGE_MULTIPLE_UPLOAD_REQUEST
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "다중 이미지 업로드 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ImageUploadResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "다중 이미지 업로드 성공 응답",
                                            value = ImageSwaggerConstants.IMAGE_MULTIPLE_UPLOAD_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 파일 형식 또는 크기 초과",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/upload/multiple")
    public ResponseEntity<BaseResponse<List<ImageUploadResponseDto>>> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("directory") String directory) throws IOException {
        List<ImageUploadResponseDto> response = imageService.uploadMultipleImages(files, directory);
        return ResponseEntity.ok(BaseResponse.of(response, HttpStatus.OK, "다중 이미지 업로드 성공"));
    }

    @Operation(
            summary = "이미지 조회",
            description = "이미지 ID를 이용해 이미지 URL을 조회합니다.",
            parameters = {
                    @Parameter(name = "imgIdx", description = "조회할 이미지 ID", example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이미지 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "이미지 조회 성공 응답",
                                            value = ImageSwaggerConstants.IMAGE_FIND_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "이미지를 찾을 수 없음",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/find/{imgIdx}")
    public ResponseEntity<BaseResponse<String>> getImage(@PathVariable Integer imgIdx) {
        try {
            String result = imageService.find(imgIdx);
            return ResponseEntity.ok(BaseResponse.of(result, HttpStatus.OK, "이미지 조회 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.of(null, HttpStatus.NOT_FOUND, e.getMessage()));
        }
    }

    @Operation(
            summary = "이미지 삭제",
            description = "이미지 ID를 이용해 이미지를 소프트 삭제하고 파일 시스템에서 제거합니다.",
            parameters = {
                    @Parameter(name = "imgIdx", description = "삭제할 이미지 ID", example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "이미지 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "이미지 삭제 성공 응답",
                                            value = ImageSwaggerConstants.IMAGE_DELETE_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "이미지를 찾을 수 없음",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping("/{imgIdx}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer imgIdx) {
        try {
            imageService.deleteImage(imgIdx);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}