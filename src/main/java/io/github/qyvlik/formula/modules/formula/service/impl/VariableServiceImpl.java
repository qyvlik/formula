package io.github.qyvlik.formula.modules.formula.service.impl;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.formula.modules.formula.model.CalculateVariable;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;
import io.github.qyvlik.formula.modules.formula.service.VariableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class VariableServiceImpl implements VariableService {

    @Value("${redis.prefix}")
    private String redisPrefix;

    // index
    public static final String market_price_symbol = "idx:market:symbol:";

    // data
    public static final String data_market_price = "data:market:price:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public CalculateVariable getVariableValue(String variableName) {
        final String fullDataKey = redisPrefix + data_market_price + variableName;
        String val = stringRedisTemplate.opsForValue().get(fullDataKey);
        if (StringUtils.isBlank(val)) {
            return null;
        }
        MarketPrice marketPrice = JSON.parseObject(val, MarketPrice.class);
        CalculateVariable calculateVariable = new CalculateVariable();
        calculateVariable.setName(variableName);
        calculateVariable.setMarket(marketPrice);
        calculateVariable.setValue(marketPrice.getPrice());
        return calculateVariable;
    }

    @Override
    public MarketPrice getMarketPriceByExchangePriority(String base, String quote, List<String> exchanges) {
        final HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        final String symbol = base + "_" + quote;
        final String fullIdxKey = redisPrefix + market_price_symbol + symbol.toLowerCase();
        for (String exchange : exchanges) {
            String val = hashOperations.get(fullIdxKey, exchange);
            if (StringUtils.isNotBlank(val)) {
                return JSON.parseObject(val, MarketPrice.class);
            }
        }

        List<String> randomExchanges = hashOperations.randomKeys(fullIdxKey, 1);
        if (CollectionUtils.isEmpty(randomExchanges)) {
            return null;
        }
        for (String exchange : randomExchanges) {
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
        final String fullDataKey = redisPrefix + data_market_price + variableName.toLowerCase();
        stringRedisTemplate.opsForValue().set(fullDataKey, val);

        final String fullIdxKey = redisPrefix + market_price_symbol + symbol.toLowerCase();
        final String field = marketPrice.getExchange();
        stringRedisTemplate.opsForHash().put(fullIdxKey, field, val);
    }
}
