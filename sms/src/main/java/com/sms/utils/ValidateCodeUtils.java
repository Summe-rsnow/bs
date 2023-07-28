package com.sms.utils;

import java.util.Random;

/**
 * 随机生成验证码工具类
 */
public class ValidateCodeUtils {
    private static final int DEFAULT_LENGTH = 4; // 默认验证码长度为4位
    private static final char[] NUMERIC_CHARS = "0123456789".toCharArray();

    /**
     * 生成指定长度的纯数字验证码
     * @param length
     * @return
     */
    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMERIC_CHARS[random.nextInt(NUMERIC_CHARS.length)]);
        }
        return sb.toString();
    }

    /**
     * 生成默认长度的纯数字验证码
     * @return
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }
}
