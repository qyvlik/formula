package io.github.qyvlik.formula.common.config;


import io.github.qyvlik.formula.modules.formula.service.VariableService;
import io.github.qyvlik.formula.modules.formula.service.impl.VariableServiceMemoryImpl;
import io.github.qyvlik.formula.modules.formula.service.impl.VariableServiceRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class FormulaConfig {

    @Value("${redis.prefix}")
    private String redisPrefix;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ConditionalOnProperty(value = "formula.variable-type", havingValue = "redis")
    @Bean
    public VariableService variableServiceRedisImpl() {
        return new VariableServiceRedisImpl(stringRedisTemplate, redisPrefix);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "formula.variable-type", havingValue = "memory")
    @Bean
    public VariableService variableServiceMemoryImpl() {
        return new VariableServiceMemoryImpl();
    }
}
