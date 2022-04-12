package io.github.qyvlik.formula.modules.formula.service.impl;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.formula.modules.formula.model.CalculateVariable;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;
import io.github.qyvlik.formula.modules.formula.service.VariableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

public class VariableServiceRedisImpl implements VariableService {

    // index
    public static final String IDX_MARKET_SYMBOL = "idx:market:symbol:";

    // data
    public static final String DATA_MARKET_PRICE = "data:market:price:";

    private final String redisPrefix;

    private final StringRedisTemplate stringRedisTemplate;

    public VariableServiceRedisImpl(StringRedisTemplate stringRedisTemplate, String redisPrefix) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisPrefix = redisPrefix;
    }

    public MarketPrice getVariableValue(String variableName) {
        final String fullDataKey = redisPrefix + DATA_MARKET_PRICE + variableName;
        String val = stringRedisTemplate.opsForValue().get(fullDataKey);
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return JSON.parseObject(val, MarketPrice.class);
    }

    @Override
    public MarketPrice getMarketPriceByExchangePriority(String base, String quote, List<String> exchanges) {
        final HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        final String symbol = base + "_" + quote;
        final String fullIdxKey = redisPrefix + IDX_MARKET_SYMBOL + symbol.toLowerCase();
        for (String exchange : exchanges) {
            String val = hashOperations.get(fullIdxKey, exchange);
            if (StringUtils.isNotBlank(val)) {
                return JSON.parseObject(val, MarketPrice.class);
            }
        }
        return null;
    }

    @Override
    public void updateMarketPrice(MarketPrice marketPrice) {
        final String val = JSON.toJSONString(marketPrice);
        final String symbol = marketPrice.getBase() + "_" + marketPrice.getQuote();
        final String variableName = marketPrice.getExchange() + "_" + symbol;
        final String fullDataKey = redisPrefix + DATA_MARKET_PRICE + variableName.toLowerCase();
        stringRedisTemplate.opsForValue().set(fullDataKey, val);

        final String fullIdxKey = redisPrefix + IDX_MARKET_SYMBOL + symbol.toLowerCase();
        final String field = marketPrice.getExchange();
        stringRedisTemplate.opsForHash().put(fullIdxKey, field, val);
    }
}
