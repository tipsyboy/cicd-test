package com.dabom.chat.constansts;

public final class SwaggerConstants {
    public static final String CREATE_CHAT_ROOM_RESPONSE = """
            {
                "data": {
                    "chatRoomId": 1,
                    "opponent": {
                        "name": "opponent_name",
                        "profileImage": "opponent_profile_image_url"
                    }
                },
                "code": 200,
                "message": "채팅방 생성 성공"
            }
            """;

    public static final String FIND_CHAT_ROOM_RESPONSE = """
            {
                "data": [
                    {
                        "chatRoomId": 1,
                        "opponent": {
                            "name": "opponent_name",
                            "profileImage": "opponent_profile_image_url"
                        },
                        "lastMessage": "last message content",
                        "lastMessageTime": "2025-09-05T10:00:00"
                    }
                ],
                "code": 200,
                "message": "채팅방 조회 성공"
            }
            """;

    public static final String NEW_MESSAGE_REQUEST = """
            {
                "message": "새로운 메시지 내용"
            }
            """;

    public static final String GET_MESSAGES_RESPONSE = """
            {
                "data": [
                    {
                        "sender": "sender_name",
                        "message": "message content",
                        "sendDate": "2025-09-05T10:00:00"
                    }
                ],
                "code": 200,
                "message": "메시지 조회 성공"
            }
            """;
}
