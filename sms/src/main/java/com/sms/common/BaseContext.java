package com.sms.common;

import io.jsonwebtoken.Claims;

/**
 * 基于ThreadLocal封装的工具类，用于获取和保存当前用户id
 */

public class BaseContext {
    private static final ThreadLocal<Claims> threadLocalClaims = new ThreadLocal<>();

    public static Long getCurrentId() {
        return (Long) threadLocalClaims.get().get("id");
    }

    public static Integer getCurrentUserGrant() {
        return (Integer) threadLocalClaims.get().get("userGrant");
    }

    public static void setCurrentClaims(Claims claims) {
        threadLocalClaims.set(claims);
    }
}