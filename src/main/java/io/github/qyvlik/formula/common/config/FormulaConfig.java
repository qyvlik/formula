package io.github.qyvlik.formula.common.config;


import io.github.qyvlik.formula.common.properties.FormulaProperties;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaCalculatorImpl;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaVariableMapImpl;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaVariableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FormulaProperties.class)
public class FormulaConfig {


    private FormulaProperties properties;

    public FormulaConfig(FormulaProperties properties) {
        this.properties = properties;
    }


    @Bean
    public FormulaCalculator formulaCalculator(
            @Autowired FormulaVariableServiceImpl formulaVariableService) {
        FormulaCalculatorImpl formulaCalculator = new FormulaCalculatorImpl();

        formulaCalculator.setAliasMap(properties.getVariableAlias());

        if (properties.getVariableType() == null ||
                properties.getVariableType().equals(FormulaProperties.VariableServiceType.redis)) {
            formulaCalculator.setFormulaVariableService(formulaVariableService);
        } else {
            formulaCalculator.setFormulaVariableService(new FormulaVariableMapImpl());
        }

        return formulaCalculator;
    }
}
