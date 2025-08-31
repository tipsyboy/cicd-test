package com.dabom.playlist.constansts;

public final class SwaggerConstants {

    // 플레이리스트 등록 요청
    public static final String PLAYLIST_REGISTER_REQUEST = """
            {
                "playlistTitle": "나만의 플레이리스트",
                "videoIds": [1, 2, 3]
            }
            """;

    // 플레이리스트 등록 응답
    public static final String PLAYLIST_REGISTER_RESPONSE = """
            {
                "data": 1,
                "code": 200,
                "message": "플레이리스트 등록 되었습니다"
            }
            """;

    // 플레이리스트에 영상 추가 요청
    public static final String PLAYLIST_ADD_VIDEO_REQUEST = """
            {
                "playlistIdx": 1,
                "videoIdx": 4
            }
            """;

    // 플레이리스트에 영상 추가 응답
    public static final String PLAYLIST_ADD_VIDEO_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "영상 추가 완료"
            }
            """;

    // 플레이리스트 목록 조회 응답
    public static final String PLAYLIST_LIST_RESPONSE = """
            {
                "data": [
                    {
                        "idx": 1,
                        "playlistTitle": "나만의 플레이리스트",
                        "createdAt": "2025-08-31 10:00:00",
                        "updatedAt": "2025-08-31 10:00:00",
                        "isModified": false,
                        "items": []
                    },
                    {
                        "idx": 2,
                        "playlistTitle": "운동할 때 듣는 음악",
                        "createdAt": "2025-08-30 15:30:00",
                        "updatedAt": "2025-08-30 15:30:00",
                        "isModified": false,
                        "items": []
                    }
                ],
                "code": 200,
                "message": "플레이리스트 조회 완료"
            }
            """;

    // 플레이리스트 상세 조회 응답
    public static final String PLAYLIST_DETAILS_RESPONSE = """
            {
                "data": {
                    "playlistTitle": "나만의 플레이리스트",
                    "videos": [
                        {
                            "idx": 1,
                            "title": "영상 제목 1",
                            "savedPath": "/path/to/video1.mp4",
                            "averageScore": 4.5
                        },
                        {
                            "idx": 2,
                            "title": "영상 제목 2",
                            "savedPath": "/path/to/video2.mp4",
                            "averageScore": 3.8
                        }
                    ]
                },
                "code": 200,
                "message": "플레이리스트 상세 조회 완료"
            }
            """;

    // 플레이리스트 수정 요청
    public static final String PLAYLIST_UPDATE_REQUEST = """
            {
                "playlistTitle": "수정된 플레이리스트 제목"
            }
            """;

    // 플레이리스트 수정 응답
    public static final String PLAYLIST_UPDATE_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "플레이리스트 수정 완료"
            }
            """;

    // 플레이리스트 삭제 응답
    public static final String PLAYLIST_DELETE_RESPONSE = """
            {
                "data": null,
                "code": 200,
                "message": "플레이리스트 삭제 완료"
            }
            """;
}