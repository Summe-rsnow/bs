package com.sms.utils;

import java.util.Random;


/**
 * @author sssnow
 */
public class ValidateCodeUtils {
    private static final int DEFAULT_NUMERIC_LENGTH = 4; // 默认纯数字验证码长度为4位
    private static final int DEFAULT_ALPHANUMERIC_LENGTH = 4; // 默认数字加字母验证码长度为4位
    private static final char[] NUMERIC_CHARS = "0123456789".toCharArray();
    private static final char[] ALPHANUMERIC_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /**
     * 生成指定长度的纯数字验证码
     *
     * @param length 验证码长度
     * @return 生成的验证码
     */
    public static String generateNumericCode(int length) {
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
     *
     * @return 生成的验证码
     */
    public static String generateNumericCode() {
        return generateNumericCode(DEFAULT_NUMERIC_LENGTH);
    }

    /**
     * 生成指定长度的数字加字母验证码
     *
     * @param length 验证码长度
     * @return 生成的验证码
     */
    public static String generateAlphanumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC_CHARS[random.nextInt(ALPHANUMERIC_CHARS.length)]);
        }
        return sb.toString();
    }

    /**
     * 生成默认长度的数字加字母验证码
     *
     * @return 生成的验证码
     */
    public static String generateAlphanumericCode() {
        return generateAlphanumericCode(DEFAULT_ALPHANUMERIC_LENGTH);
    }
}