package com.dabom.member.contants;

public abstract class JWTConstants {
    public static final String TOKEN_IDX = "idx";
    public static final String TOKEN_NAME = "name";
    public static final String TOKEN_USER_ROLE = "role";
    public static final String ACCESS_TOKEN = "DABOM_AT";
    public static final String REFRESH_TOKEN = "DABOM_RT";
    public static final Integer ACCESS_TOKEN_EXP = 30 * 60;
    public static final Integer REFRESH_TOKEN_EXP = 12 * 60 * 60;
}
