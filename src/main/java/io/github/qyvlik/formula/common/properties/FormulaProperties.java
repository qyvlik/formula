package io.github.qyvlik.formula.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "formula")
public class FormulaProperties {
    private Map<String, String> variableAlias;
    private List<String> accessTokens;
    private VariableServiceType variableType;

    public enum VariableServiceType {
        redis,
        memory
    }
}
