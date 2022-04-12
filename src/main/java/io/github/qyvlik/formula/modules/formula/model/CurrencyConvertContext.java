package io.github.qyvlik.formula.modules.formula.model;

import com.google.common.collect.Maps;
import io.github.qyvlik.formula.modules.formula.service.VariableService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class CurrencyConvertContext {
    public static final int MAX_SCALE = 18;

    private final Map<String, MarketPrice> cache = Maps.newHashMap();
    private final VariableService variableService;
    private final int scale;

    public CurrencyConvertContext(VariableService variableService, Integer scale) {
        this.variableService = variableService;
        this.scale = scale != null ? scale : MAX_SCALE;
    }

    public ProcessingPrice getProcessingPrice(String source, String target, List<String> exchanges) {
        MarketPrice p1 = getMarketPrice(source, target, exchanges);
        if (p1 != null) {
            return ProcessingPrice.builder()
                    .market(p1)
                    .source(source)
                    .target(target)
                    .price(p1.getPrice())
                    .build();
        }
        MarketPrice p2 = getMarketPrice(target, source, exchanges);
        if (p2 != null) {
            final BigDecimal price = BigDecimal.ONE.divide(p2.getPrice(), scale, RoundingMode.DOWN);
            return ProcessingPrice.builder()
                    .market(p2)
                    .source(source)
                    .target(target)
                    .price(price)
                    .build();
        }
        return null;
    }

    private MarketPrice getMarketPrice(final String base, final String quote, final List<String> exchanges) {
        String key = base + "_" + quote;
        return cache.computeIfAbsent(key,
                k -> variableService.getMarketPriceByExchangePriority(base, quote, exchanges));
    }
}
