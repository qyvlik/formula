package io.github.qyvlik.formula.common.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    private Result(int code, String message, T data) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return code == Code.SUCCESS;
    }

    public Result() {
        this(Code.SYSTEM_ERROR, null, null);
    }

    public static <E> Result<E> success(E data) {
        return new Result<E>(Code.SUCCESS, null, data);
    }

    public static <E> Result<?> failure(Result<?> res) {
        return new Result<E>(res.getCode(), res.getMessage(), null);
    }

    public static <E> Result<E> failure(int code) {
        return new Result<E>(code, null, null);
    }

    public static <E> Result<E> failure(int code, String message) {
        return new Result<E>(code, message, null);
    }

    public static <E> Result<E> failure(int code, String message, E data) {
        return new Result<E>(code, message, data);
    }
}
