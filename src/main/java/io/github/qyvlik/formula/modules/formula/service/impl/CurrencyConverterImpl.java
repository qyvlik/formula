package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Lists;
import io.github.qyvlik.formula.modules.formula.cmd.CurrencyConvertCmd;
import io.github.qyvlik.formula.modules.formula.model.CurrencyConvertResultData;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;
import io.github.qyvlik.formula.modules.formula.model.ProcessingPrice;
import io.github.qyvlik.formula.modules.formula.service.CurrencyConverter;
import io.github.qyvlik.formula.modules.formula.service.VariableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class CurrencyConverterImpl implements CurrencyConverter {
    public static final int MAX_PRICE_SCALE = 18;

    private VariableService variableService;

    public CurrencyConverterImpl(@Autowired VariableService variableService) {
        this.variableService = variableService;
    }

    @Override
    public CurrencyConvertResultData currencyConvert(CurrencyConvertCmd cmd) {
        final String source = cmd.getSource().toLowerCase();
        final String target = cmd.getTarget().toLowerCase();
        if (StringUtils.isBlank(target) || StringUtils.isBlank(source)) {
            return CurrencyConvertResultData.failure(cmd);
        }

        if (target.equalsIgnoreCase(source)) {
            return CurrencyConvertResultData.failure(cmd);
        }

        List<String> midCurrencyList = !CollectionUtils.isEmpty(cmd.getMiddles()) ?
                cmd.getMiddles() : Lists.newArrayList("usdt", "btc", "eth", "bnb", "ht", "okb");
        List<String> exchangeList = !CollectionUtils.isEmpty(cmd.getExchanges()) ?
                cmd.getExchanges() : Lists.newArrayList("huobi", "binance", "okcoin");

        // 1. A -> B, B -> A
        // 2. A -> X -> B
        // 3. A -> X1 -> X2 -> B
        // X, X1, X2 指中间币种 USDT, BTC, ETH, BNB, HT, OKB

        ProcessingPrice fastPrice = getProcessingPrice(source, target, exchangeList);
        if (fastPrice != null) {
            BigDecimal price = fastPrice.getPrice();
            if (cmd.getScale() != null) {
                price = price.setScale(cmd.getScale(), RoundingMode.DOWN);
            }

            return CurrencyConvertResultData.success(cmd, price, Lists.newArrayList(fastPrice));
        }

        for (String midCurrency : midCurrencyList) {
            ProcessingPrice p1 = getProcessingPrice(source, midCurrency, exchangeList);
            ProcessingPrice p2 = getProcessingPrice(midCurrency, target, exchangeList);
            if (p1 != null && p2 != null) {
                BigDecimal price1 = p1.getPrice();
                BigDecimal price2 = p2.getPrice();
                BigDecimal price = price1.multiply(price2);
                if (cmd.getSource() != null) {
                    price = price.setScale(cmd.getScale(), RoundingMode.DOWN);
                }

                return CurrencyConvertResultData.success(cmd, price, Lists.newArrayList(p1, p2));
            }
        }

        List<String> midPairList = Lists.newArrayList();
        for (String midCurrency1 : midCurrencyList) {
            for (String midCurrency2 : midCurrencyList) {
                if (midCurrency1.equals(midCurrency2)) {
                    continue;
                }
                String midPair = (midCurrency1 + "_" + midCurrency2).toLowerCase();
                midPairList.add(midPair);
            }
        }

        // A -> X1 -> X2 -> B
        for (String midPair : midPairList) {
            String[] currencies = midPair.split("_");
            String x1 = currencies[0];
            String x2 = currencies[1];
            ProcessingPrice p1 = getProcessingPrice(source, x1, exchangeList);
            ProcessingPrice p2 = getProcessingPrice(x1, x2, exchangeList);
            ProcessingPrice p3 = getProcessingPrice(x2, target, exchangeList);
            if (p1 != null && p2 != null && p3 != null) {
                BigDecimal price1 = p1.getPrice();
                BigDecimal price2 = p2.getPrice();
                BigDecimal price3 = p3.getPrice();
                BigDecimal price = price1.multiply(price2).multiply(price3);
                if (cmd.getSource() != null) {
                    price = price.setScale(cmd.getScale(), RoundingMode.DOWN);
                }
                return CurrencyConvertResultData.success(cmd, price, Lists.newArrayList(p1, p2, p3));
            }
        }

        return CurrencyConvertResultData.failure(cmd);
    }

    private ProcessingPrice getProcessingPrice(String source, String target, List<String> exchanges) {
        MarketPrice p1 = variableService.getMarketPriceByExchangePriority(source, target, exchanges);
        if (p1 != null) {
            return ProcessingPrice.builder()
                    .market(p1)
                    .source(source)
                    .target(target)
                    .price(p1.getPrice())
                    .build();
        }
        MarketPrice p2 = variableService.getMarketPriceByExchangePriority(target, source, exchanges);
        if (p2 != null) {
            final BigDecimal price = BigDecimal.ONE.divide(p2.getPrice(), MAX_PRICE_SCALE, RoundingMode.DOWN);
            return ProcessingPrice.builder()
                    .market(p2)
                    .source(source)
                    .target(target)
                    .price(price)
                    .build();
        }
        return null;
    }

//    private MarketPrice getMarketPriceByExchangePriority(String base, String quote, List<String> exchanges) {
//        final String symbol = (base + "_" + quote).toLowerCase();
//        Map<String, MarketPrice> map = prices.get(symbol);
//        if (CollectionUtils.isEmpty(map)) {
//            return null;
//        }
//        for (String exchange : exchanges) {
//            if (map.containsKey(exchange)) {
//                return map.get(exchange);
//            }
//        }
//        return map.values().stream().iterator().next();
//    }

}
