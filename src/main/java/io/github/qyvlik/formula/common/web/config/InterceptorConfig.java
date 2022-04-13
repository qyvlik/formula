package io.github.qyvlik.formula.common.web.config;

import io.github.qyvlik.formula.common.web.interceptor.OpenAccessInterceptor;
import io.github.qyvlik.formula.common.web.processor.I18nResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private OpenAccessInterceptor openAccessInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(openAccessInterceptor)
                .addPathPatterns("/api/v1/**");
        ;
    }
}
