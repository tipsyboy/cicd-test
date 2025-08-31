package com.dabom.search.constants;

public final class SwaggerConstants {
    public static final String SEARCH_VIDEOS_RESPONSE = """
            {
                "data": {
                    "content": [
                        {
                            "videoId": 1,
                            "title": "Java 프로그래밍 기초 강의",
                            "description": "Java 프로그래밍의 기본 개념을 다루는 강의입니다.",
                            "thumbnailUrl": "https://example.com/thumbnail1.jpg",
                            "channelName": "코딩강의채널",
                            "viewCount": 15230,
                            "createdAt": "2024-12-01 10:30:00"
                        },
                        {
                            "videoId": 2,
                            "title": "Spring Boot 실전 프로젝트",
                            "description": "Spring Boot를 활용한 실제 프로젝트 개발",
                            "thumbnailUrl": "https://example.com/thumbnail2.jpg",
                            "channelName": "개발자TV",
                            "viewCount": 8945,
                            "createdAt": "2024-11-28 14:20:00"
                        }
                    ],
                    "hasNext": true,
                    "totalCount": 25
                },
                "code": 200,
                "message": "비디오 조회 완료"
            }
            """;
}