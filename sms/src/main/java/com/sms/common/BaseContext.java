package com.sms.common;

/**
 * 基于ThreadLocal封装的工具类，用于获取和保存当前用户id
 */

public class BaseContext {
    private static final ThreadLocal<Long> threadLocalLong = new ThreadLocal<>();

    public static Long getCurrentId() {
        return threadLocalLong.get();
    }

    public static void setCurrentId(Long id) {
        threadLocalLong.set(id);
    }
}