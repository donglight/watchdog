package org.donglight.watchdog.common.util;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<br>
 * 前端请求通用返回类
 *
 * @author donglight
 * @date 2019/4/19
 * @since 1.0.0
 */
@Getter
@Setter
public class Response<T> implements Serializable {


    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误数据
     */
    private T data;

    public Response() {

    }
    private Response(int code) {
        this(code, ResponseEnum.SUCCESS.getMessage());
    }

    private Response(int code, String message) {
        this(code, message, null);
    }

    private Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Response(int code, T data) {
        this(code, ResponseEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Response<T> ok() {
        return new Response<>(ResponseEnum.SUCCESS.getCode());
    }

    public static <T> Response<T> ok(String message) {
        return new Response<>(ResponseEnum.SUCCESS.getCode(), message);
    }

    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(ResponseEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(ResponseEnum.SUCCESS.getCode(), data);
    }

    public static <T> Response<T> ok(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

    public static <T> Response<T> ok(ResponseEnum responseEnum) {
        return new Response<>(responseEnum.getCode(), responseEnum.getMessage());
    }

    public static <T> Response<T> ok(ResponseEnum responseEnum, T data) {
        return new Response<>(responseEnum.getCode(), responseEnum.getMessage(), data);
    }

    public static <T> Response<T> exception(ResponseEnum responseCode) {
        return new Response<>(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> Response<T> exception(ResponseEnum responseCode, T data) {
        return new Response<>(responseCode.getCode(), responseCode.getMessage(), data);
    }

    public static <T> Response<T> exception(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

}
