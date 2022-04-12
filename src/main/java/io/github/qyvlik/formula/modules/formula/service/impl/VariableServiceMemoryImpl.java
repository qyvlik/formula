package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Maps;
import io.github.qyvlik.formula.modules.formula.model.CalculateVariable;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;
import io.github.qyvlik.formula.modules.formula.service.VariableService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class VariableServiceMemoryImpl implements VariableService {

    private final ConcurrentMap<String, Map<String, MarketPrice>> idxMarketSymbol = Maps.newConcurrentMap();
    private final ConcurrentMap<String, MarketPrice> dataMarketPrice = Maps.newConcurrentMap();

    public VariableServiceMemoryImpl() {

    }

    public MarketPrice getVariableValue(String variableName) {
        MarketPrice originMarketPrice = dataMarketPrice.get(variableName);
        if (originMarketPrice == null) {
            return null;
        }
        MarketPrice marketPrice = new MarketPrice();
        BeanUtils.copyProperties(originMarketPrice, marketPrice);
        return marketPrice;
    }

    @Override
    public MarketPrice getMarketPriceByExchangePriority(String base, String quote, List<String> exchanges) {
        final String symbol = (base + "_" + quote).toLowerCase();
        Map<String, MarketPrice> map = idxMarketSymbol.get(symbol);
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }
        for (String exchange : exchanges) {
            if (map.containsKey(exchange)) {
                return map.get(exchange);
            }
        }
        return null;
    }

    @Override
    public void updateMarketPrice(MarketPrice originMarketPrice) {
        MarketPrice marketPrice = new MarketPrice();
        BeanUtils.copyProperties(originMarketPrice, marketPrice);
        final String symbol = (marketPrice.getBase() + "_" + marketPrice.getQuote()).toLowerCase();
        final String variableName = (marketPrice.getExchange() + "_" + symbol).toLowerCase();

        dataMarketPrice.put(variableName, marketPrice);

        Map<String, MarketPrice> priceOfExchange = idxMarketSymbol.computeIfAbsent(symbol, k -> Maps.newConcurrentMap());
        priceOfExchange.put(marketPrice.getExchange(), marketPrice);
    }
}
