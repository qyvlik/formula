package io.github.qyvlik.formula.common.web.processor;

import io.github.qyvlik.formula.common.base.Result;
import io.github.qyvlik.formula.common.base.ResultMessageUtils;
import io.github.qyvlik.formula.modules.formula.model.ErrCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class I18nResponseProcessor extends RequestResponseBodyMethodProcessor {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;

    public I18nResponseProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    protected <T> void writeWithMessageConverters(@Nullable T value,
                                                  MethodParameter returnType,
                                                  ServletServerHttpRequest inputMessage,
                                                  ServletServerHttpResponse outputMessage)
            throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        if (value instanceof Result) {
            Result<?> r = (Result<?>) value;
            if (!r.isSuccess() && r.getCode() > ErrCode.MIN_ERR_CODE) {
                Locale locale = localeResolver.resolveLocale(inputMessage.getServletRequest());
                String[] args = null;
                try {
                    args = ResultMessageUtils.parse(r.getMessage());
                } catch (Exception ignored) {
                }
                String i18nMessage = messageSource.getMessage(r.getCode() + "", args, locale);
                r.setMessage(i18nMessage);
            }
        }

        super.writeWithMessageConverters(value, returnType, inputMessage, outputMessage);
    }
}
