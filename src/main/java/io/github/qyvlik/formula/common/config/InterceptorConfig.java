package io.github.qyvlik.formula.common.config;

import io.github.qyvlik.formula.common.interceptor.OpenAccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
