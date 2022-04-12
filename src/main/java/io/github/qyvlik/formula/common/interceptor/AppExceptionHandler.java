package io.github.qyvlik.formula.common.interceptor;

import io.github.qyvlik.formula.common.base.AppException;
import io.github.qyvlik.formula.common.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public Result<?> appExceptionHandle(AppException e) {
        log.error("appExceptionHandle", e);

        return e.getResult();
    }

}
