package io.github.qyvlik.formula.common.base;

import lombok.Data;

@Data
public class AppException extends RuntimeException {
    private final Result<?> result;

    public AppException(Result<?> result) {
        this(null, result);
    }

    public AppException(Throwable throwable, Result<?> result) {
        super(throwable != null ? throwable.getMessage() : (result != null ? result.getMessage() : null), throwable);
        this.result = result;
    }

    public static AppException create(int code) {
        return new AppException(Result.failure(code));
    }

    public static AppException create(int code, String message) {
        return new AppException(Result.failure(code, message));
    }

    public static AppException create(int code, Throwable throwable) {
        return new AppException(throwable, Result.failure(code));
    }

    public static <E> AppException create(int code, String message, E data) {
        return new AppException(Result.failure(code, message, data));
    }
}
