package io.github.qyvlik.formula.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "formula")
public class FormulaProperties {
    private Map<String, String> variableAlias;
    private List<String> accessTokens;

    public Map<String, String> getVariableAlias() {
        return variableAlias;
    }

    public void setVariableAlias(Map<String, String> variableAlias) {
        this.variableAlias = variableAlias;
    }


    public List<String> getAccessTokens() {
        return accessTokens;
    }

    public void setAccessTokens(List<String> accessTokens) {
        this.accessTokens = accessTokens;
    }
}
