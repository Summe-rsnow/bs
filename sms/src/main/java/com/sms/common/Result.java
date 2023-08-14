package com.sms.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sssnow
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据


    private Result() {
    }

    public static <T> Result<T> success(T object, String msg) {
        Result<T> result = new Result<>();
        result.data = object;
        result.msg = msg;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static Result<String> success() {
        Result<String> result = new Result<>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }


}
