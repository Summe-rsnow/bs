package com.sms.common;

import io.jsonwebtoken.Claims;

/**
 * 基于ThreadLocal封装的工具类，用于获取和保存当前用户id
 *
 * @author sssnow
 */

public class BaseContext {
    private static final ThreadLocal<Claims> THREAD_LOCAL_CLAIMS = new ThreadLocal<>();

    public static Long getCurrentId() {
        return (Long) THREAD_LOCAL_CLAIMS.get().get("id");
    }

    public static Integer getCurrentUserGrant() {
        return (Integer) THREAD_LOCAL_CLAIMS.get().get("userGrant");
    }

    public static void setCurrentClaims(Claims claims) {
        THREAD_LOCAL_CLAIMS.set(claims);
    }
}