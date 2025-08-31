package com.dabom.likes.constansts;

public final class SwaggerConstants {


    // 좋아요 관련 상수들
    public static final String LIKE_TOGGLE_REQUEST = """
            {
                "targetId": 1,
                "targetType": "BOARD"
            }
            """;

    public static final String LIKE_TOGGLE_RESPONSE = """
            {
                "data": "좋아요가 추가되었습니다.",
                "code": 200,
                "message": "좋아요가 추가되었습니다."
            }
            """;

    public static final String LIKE_COUNT_RESPONSE = """
            {
                "data": {
                    "targetId": 1,
                    "targetType": "BOARD",
                    "likeCount": 15
                },
                "code": 200,
                "message": "좋아요 개수 조회 성공"
            }
            """;


}