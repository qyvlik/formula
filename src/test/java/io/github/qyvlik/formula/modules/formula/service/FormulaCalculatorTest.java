package io.github.qyvlik.formula.modules.formula.service;

import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaCalculatorImpl;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaVariableMapImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

public class FormulaCalculatorTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FormulaCalculatorImpl formulaCalculator;
    private FormulaVariableService formulaVariableService;

    @Before
    public void setup() {
        formulaVariableService = new FormulaVariableMapImpl();
        formulaCalculator = new FormulaCalculatorImpl();
        formulaCalculator.setFormulaVariableService(formulaVariableService);
        formulaCalculator.setAliasMap(null);
    }

    @Test
    public void calculateSuccess() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        formulaVariableService.updateFormulaVariable(new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                60 * 1000L
        ));

        String formula = "huobipro_btc_usdt*1.01";

        FormulaResult formulaResult = formulaCalculator.calculate(formula);

        logger.info("calculate :{}", formulaResult);
    }

    @Test
    public void calculateFailure() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        formulaVariableService.updateFormulaVariable(new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                60 * 1000L
        ));

        formulaVariableService.deleteFormulaVariable("huobipro_btc_usdt");

        String formula = "huobipro_btc_usdt*1.01";

        try {
            FormulaResult formulaResult = formulaCalculator.calculate(formula);
            logger.info("calculate :{}", formulaResult);
        } catch (Exception e) {
            logger.info("e", e);
            Assert.assertTrue("", e.getMessage().toLowerCase().contains("variable huobipro_btc_usdt not exist"));
        }

    }

    @Test
    public void test() {
        System.out.println(UUID.randomUUID().toString());
    }
}