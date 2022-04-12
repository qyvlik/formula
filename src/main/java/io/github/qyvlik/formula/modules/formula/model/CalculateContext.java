package io.github.qyvlik.formula.modules.formula.model;

import com.google.common.collect.Maps;
import io.github.qyvlik.formula.modules.formula.service.VariableService;

import java.util.Map;

public class CalculateContext {

    private final Map<String, CalculateVariable> cache = Maps.newHashMap();
    private final VariableService variableService;

    public CalculateContext(VariableService variableService) {
        this.variableService = variableService;
    }

    // ${exchange}_${base}_${quote}
    public CalculateVariable getVariableValue(String variable) {
        CalculateVariable val = cache.get(variable);
        if (val != null) {
            return val;
        }

        CalculateVariable valFromDB = loadFromDB(variable);
        cache.put(variable, valFromDB);
        return valFromDB;
    }

    private CalculateVariable loadFromDB(String variable) {
        return variableService.getVariableValue(variable);
    }
}
