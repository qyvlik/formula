package io.github.qyvlik.formula.modules.formula.service.impl;

import io.github.qyvlik.formula.modules.formula.model.CalculateContext;
import io.github.qyvlik.formula.modules.formula.model.CalculateResultData;
import io.github.qyvlik.formula.modules.formula.service.VariableService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class FormulaCalculatorTest {
    private final VariableService variableService = new VariableServiceMemoryImpl();

    @Test
    public void testBlank() {
        CalculateContext context = new CalculateContext(variableService);
        FormulaCalculator calculator1 = new FormulaCalculator();
        calculator1.calculate("", context);
    }

    @Test
    public void testCalculate() {
        CalculateContext context = new CalculateContext(variableService);
        FormulaCalculator calculator1 = new FormulaCalculator();
        CalculateResultData result = calculator1.calculate("1*1", context);
        log.info("result= {}", result);
    }

    @Test
    public void testCalculateVariable() {
        CalculateContext context = new CalculateContext(variableService);

        FormulaCalculator calculator1 = new FormulaCalculator();
        CalculateResultData result = calculator1.calculate("a*b", context);
        log.info("result= {}", result);
    }

    @Test
    public void testCalculateFunction() {
        CalculateContext context = new CalculateContext(variableService);

        FormulaCalculator calculator1 = new FormulaCalculator();

        CalculateResultData result = calculator1.calculate("max(a-b, a+b)", context);
        log.info("result= {}", result);
    }
}