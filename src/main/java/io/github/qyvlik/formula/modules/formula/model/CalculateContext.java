package io.github.qyvlik.formula.modules.formula.model;

import com.google.common.collect.Maps;
import io.github.qyvlik.formula.modules.formula.service.VariableService;

import java.util.Map;

public class CalculateContext {

    private final Map<String, MarketPrice> cache = Maps.newHashMap();
    private final VariableService variableService;

    public CalculateContext(VariableService variableService) {
        this.variableService = variableService;
    }

    // ${exchange}_${base}_${quote}
    public CalculateVariable getVariableValue(final String variableName) {
        MarketPrice marketPrice = cache.computeIfAbsent(variableName,
                k -> variableService.getVariableValue(variableName));
        if (marketPrice == null) {
            return null;
        }

        CalculateVariable calculateVariable = new CalculateVariable();
        calculateVariable.setName(variableName);
        calculateVariable.setMarket(marketPrice);
        calculateVariable.setValue(marketPrice.getPrice());
        return calculateVariable;
    }
}
