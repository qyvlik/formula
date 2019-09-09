package io.github.qyvlik.formula.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "formula")
public class FormulaProperties {
    private Map<String, String> variableAlias;

    public Map<String, String> getVariableAlias() {
        return variableAlias;
    }

    public void setVariableAlias(Map<String, String> variableAlias) {
        this.variableAlias = variableAlias;
    }
}
