package io.github.qyvlik.formula.common.config;


import io.github.qyvlik.formula.common.properties.FormulaProperties;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaCalculatorImpl;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaVariableService;
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
            @Autowired FormulaVariableService formulaVariableService) {
        FormulaCalculatorImpl formulaCalculator = new FormulaCalculatorImpl();

        formulaCalculator.setAliasMap(properties.getVariableAlias());
        formulaCalculator.setFormulaVariableService(formulaVariableService);

        return formulaCalculator;
    }


}
