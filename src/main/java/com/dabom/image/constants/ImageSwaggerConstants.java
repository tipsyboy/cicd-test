package com.dabom.image.constants;

public final class ImageSwaggerConstants {
    public static final String IMAGE_UPLOAD_REQUEST = """
            {
                "file": "[이미지 파일]",
                "directory": "uploads"
            }
            """
    ;

    public static final String IMAGE_UPLOAD_RESPONSE = """
            {
                "data": {
                    "originalName": "example.jpg",
                    "imageName": "uuid1234.jpg",
                    "imagePath": "/uploads/uuid1234.jpg",
                    "imageUrl": "http://localhost:8080/Uploads/uuid1234.jpg",
                    "imageSize": 102400
                },
                "code": 200,
                "message": "이미지 업로드 성공"
            }
            """
    ;

    public static final String IMAGE_MULTIPLE_UPLOAD_REQUEST = """
            {
                "files": ["[이미지 파일1]", "[이미지 파일2]"],
                "directory": "Uploads"
            }
            """
    ;

    public static final String IMAGE_MULTIPLE_UPLOAD_RESPONSE = """
            {
                "data": [
                    {
                        "originalName": "example1.jpg",
                        "imageName": "uuid1234.jpg",
                        "imagePath": "/Uploads/uuid1234.jpg",
                        "imageUrl": "http://localhost:8080/Uploads/uuid1234.jpg",
                        "imageSize": 102400
                    },
                    {
                        "originalName": "example2.png",
                        "imageName": "uuid5678.png",
                        "imagePath": "/Uploads/uuid5678.png",
                        "imageUrl": "http://localhost:8080/Uploads/uuid5678.png",
                        "imageSize": 204800
                    }
                ],
                "code": 200,
                "message": "다중 이미지 업로드 성공"
            }
            """
    ;

    public static final String IMAGE_FIND_RESPONSE = """
            {
                "data": "http://localhost:8080/Uploads/uuid1234.jpg",
                "code": 200,
                "message": "이미지 조회 성공"
            }
            """
    ;

    public static final String IMAGE_DELETE_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "이미지 삭제 성공"
            }
            """
    ;

    public static final String IMAGE_UPLOAD_PRESIGNED_URL_RESPONSE = """
            {
                "data": {
                    "uploadUrl": "https://s3.presigned.url/for/upload",
                    "s3Key": "s3-object-key",
                    "expiresIn": 3600
                },
                "code": 200,
                "message": "Presigned URL for image upload"
            }
            """
    ;
}