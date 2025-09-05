package com.dabom.score.constansts;

public final class SwaggerConstants {
    public static final String SCORE_REGISTER_REQUEST = """
            {
                "score": 5
            }
            """;

    public static final String SCORE_REGISTER_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "점수가 성공적으로 등록되었습니다."
            }
            """;

    public static final String GET_AVG_SCORE_RESPONSE = """
            {
                "data": {
                    "avg": 4.5
                },
                "code": 200,
                "message": "평균 점수 조회를 성공했습니다."
            }
            """;
}
