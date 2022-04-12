package io.github.qyvlik.formula.modules.formula.service;

import io.github.qyvlik.formula.modules.formula.model.CalculateVariable;
import io.github.qyvlik.formula.modules.formula.model.MarketPrice;

import java.util.List;

public interface VariableService {

    CalculateVariable getVariableValue(String variableName);

    MarketPrice getMarketPriceByExchangePriority(String base, String quote, List<String> exchanges);

    void updateMarketPrice(MarketPrice marketPrice);
}
