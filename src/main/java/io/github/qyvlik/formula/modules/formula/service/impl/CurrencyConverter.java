package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Lists;
import io.github.qyvlik.formula.modules.formula.cmd.CurrencyConvertCmd;
import io.github.qyvlik.formula.modules.formula.model.CurrencyConvertContext;
import io.github.qyvlik.formula.modules.formula.model.CurrencyConvertResultData;
import io.github.qyvlik.formula.modules.formula.model.ProcessingPrice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
public class CurrencyConverter {

    public CurrencyConvertResultData currencyConvert(CurrencyConvertCmd cmd, CurrencyConvertContext context) {
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

        ProcessingPrice fastPrice = context.getProcessingPrice(source, target, exchangeList);
        if (fastPrice != null) {
            BigDecimal price = fastPrice.getPrice();
            if (cmd.getScale() != null) {
                price = price.setScale(cmd.getScale(), RoundingMode.DOWN);
            }

            return CurrencyConvertResultData.success(cmd, price, Lists.newArrayList(fastPrice));
        }

        for (String midCurrency : midCurrencyList) {
            ProcessingPrice p1 = context.getProcessingPrice(source, midCurrency, exchangeList);
            ProcessingPrice p2 = context.getProcessingPrice(midCurrency, target, exchangeList);
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
            ProcessingPrice p1 = context.getProcessingPrice(source, x1, exchangeList);
            ProcessingPrice p2 = context.getProcessingPrice(x1, x2, exchangeList);
            ProcessingPrice p3 = context.getProcessingPrice(x2, target, exchangeList);
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
}
