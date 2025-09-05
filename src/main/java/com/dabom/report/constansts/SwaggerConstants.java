package com.dabom.report.constansts;

public final class SwaggerConstants {
    public static final String REPORT_REGISTER_REQUEST = """
            {
                "reason": "부적절한 콘텐츠",
                "memberId": 1,
                "targetId": 1,
                "targetType": "VIDEO"
            }
            """;

    public static final String REPORT_REGISTER_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "신고가 성공적으로 접수되었습니다."
            }
            """;
}
