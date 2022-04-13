package io.github.qyvlik.formula.common.interceptor;

import com.google.common.collect.Lists;
import io.github.qyvlik.formula.common.base.AppException;
import io.github.qyvlik.formula.common.base.Code;
import io.github.qyvlik.formula.common.base.Result;
import io.github.qyvlik.formula.common.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public Result<?> appExceptionHandle(AppException e) {
        final HttpServletRequest request = ServletUtils.getRequest();
        final String uri = request != null ? request.getRequestURI() : "";
        log.error("appExceptionHandle uri:{}", uri, e);
        return e.getResult();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        final List<String> errors = Lists.newArrayList();
        for (ObjectError objectError : e.getBindingResult().getAllErrors()) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                String err = fieldError.getField() + " " + fieldError.getDefaultMessage();
                errors.add(err);
            }
        }
        final String message = String.join("\n", errors);

        if (log.isWarnEnabled()) {
            final HttpServletRequest request = ServletUtils.getRequest();
            final String uri = request != null ? request.getRequestURI() : "";
            log.warn("methodArgumentNotValidException uri:{}, error:{}", uri, e.getMessage());
        }

        return Result.failure(Code.ILLEGAL_PARAM, message);
    }
}
