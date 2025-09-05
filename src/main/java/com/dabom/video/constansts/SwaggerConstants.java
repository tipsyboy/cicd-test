package com.dabom.video.constansts;

public final class SwaggerConstants {
    public static final String VIDEO_UPLOAD_REQUEST = """
            {
                "originalFilename": "my_video.mp4",
                "originalSize": 12345678,
                "contentType": "video/mp4"
            }
            """;

    public static final String VIDEO_UPLOAD_RESPONSE = """
            {
                "data": {
                    "videoIdx": 1,
                    "uploadUrl": "https://s3.presigned.url/for/upload",
                    "s3Key": "s3-object-key",
                    "expiresIn": 3600
                },
                "code": 200,
                "message": "Presigned URL for video upload"
            }
            """;

    public static final String VIDEO_METADATA_REQUEST = """
            {
                "idx": 1,
                "title": "My Awesome Video",
                "description": "A description of my awesome video.",
                "isPublic": true,
                "videoTag": "ENTERTAINMENT"
            }
            """;

    public static final String VIDEO_METADATA_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "비디오 메타데이터가 성공적으로 업데이트되었습니다."
            }
            """;

    public static final String VIDEO_INFO_RESPONSE = """
            {
                "data": {
                    "videoIdx": 1,
                    "title": "My Awesome Video",
                    "description": "A description of my awesome video.",
                    "views": 100,
                    "totalReviewerCount": 10,
                    "averageScore": 4.5,
                    "isPublicVideo": true,
                    "uploadedAt": "2025-09-05T12:00:00",
                    "thumbnailImage": "https://thumbnail.url/image.jpg"
                },
                "code": 200,
                "message": "비디오 정보 조회 성공"
            }
            """;
}
