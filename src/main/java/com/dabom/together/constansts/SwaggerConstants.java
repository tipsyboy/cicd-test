package com.dabom.together.constansts;

public final class SwaggerConstants {
    public static final String TOGETHER_CREATE_REQUEST = """
            {
                "title": "같이 봐요",
                "videoUrl": "https://example.com/video.mp4",
                "maxMemberNumber": 10,
                "isOpen": true
            }
            """;

    public static final String TOGETHER_CREATE_RESPONSE = """
            {
                "data": {
                    "togetherIdx": 1,
                    "title": "같이 봐요",
                    "maxMemberNum": 10,
                    "joinMemberNumber": 1,
                    "master": {
                        "memberId": 1,
                        "name": "master_name",
                        "profileImage": "master_profile_image"
                    },
                    "isOpen": true,
                    "userIdx": null,
                    "code": "some-uuid-code",
                    "videoUrl": "https://example.com/video.mp4"
                },
                "code": 200,
                "message": "같이보기방 생성 성공"
            }
            """;

    public static final String TOGETHER_LIST_RESPONSE = """
            {
                "data": {
                    "togethers": [
                        {
                            "togetherIdx": 1,
                            "title": "같이 봐요",
                            "maxMemberNum": 10,
                            "joinMemberNumber": 5,
                            "master": {
                                "memberId": 1,
                                "name": "master_name",
                                "profileImage": "master_profile_image"
                            },
                            "isOpen": true,
                            "userIdx": null,
                            "code": "some-uuid-code",
                            "videoUrl": "https://example.com/video.mp4"
                        }
                    ]
                },
                "code": 200,
                "message": "같이보기방 조회 성공"
            }
            """;
}
