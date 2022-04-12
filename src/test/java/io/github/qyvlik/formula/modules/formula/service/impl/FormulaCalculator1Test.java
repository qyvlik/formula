package io.github.qyvlik.formula.modules.formula.service.impl;

import io.github.qyvlik.formula.common.base.AppException;
import io.github.qyvlik.formula.modules.formula.model.CalculateContext;
import io.github.qyvlik.formula.modules.formula.model.CalculateResultData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class FormulaCalculator1Test {


    @Test
    public void testBlank() {
        FormulaCalculator1 calculator1 = new FormulaCalculator1();
        CalculateContext context = new CalculateContext();
        calculator1.calculate("", context);
    }

    @Test
    public void testCalculate() {
        FormulaCalculator1 calculator1 = new FormulaCalculator1();
        CalculateContext context = new CalculateContext();
        CalculateResultData result = calculator1.calculate("1*1", context);
        log.info("result= {}", result);
    }

    @Test
    public void testCalculateVariable() {
        FormulaCalculator1 calculator1 = new FormulaCalculator1();
        CalculateContext context = new CalculateContext();
        CalculateResultData result = calculator1.calculate("a*b", context);
        log.info("result= {}", result);
    }

    @Test
    public void testCalculateFunction() {
        FormulaCalculator1 calculator1 = new FormulaCalculator1();
        CalculateContext context = new CalculateContext();
        CalculateResultData result = calculator1.calculate("max(a-b, a+b)", context);
        log.info("result= {}", result);
    }
}