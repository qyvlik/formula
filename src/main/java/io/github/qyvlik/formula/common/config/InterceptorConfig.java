package io.github.qyvlik.formula.common.config;

import io.github.qyvlik.formula.common.interceptor.AccessTokenAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AccessTokenAuthInterceptor accessTokenAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(accessTokenAuthInterceptor)
                .addPathPatterns("/api/v1/formula/variables/update")
                .addPathPatterns("/api/v1/formula/variables/delete")
                .excludePathPatterns("/api/v1/formula/variables/names")
                .excludePathPatterns("/api/v1/formula/variables/alias")
                .excludePathPatterns("/api/v1/formula/variable/*")
                .excludePathPatterns("/api/v1/formula/eval")
        ;
    }
}
