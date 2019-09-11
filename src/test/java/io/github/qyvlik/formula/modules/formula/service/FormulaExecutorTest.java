package io.github.qyvlik.formula.modules.formula.service;

import com.google.common.collect.Maps;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaExecutor;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.util.Map;

public class FormulaExecutorTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private NashornScriptEngineFactory factory;
    private ScriptEngine engine;

    @Before
    public void setup() {
        factory = new NashornScriptEngineFactory();
        engine = factory.getScriptEngine(new String[]{"-strict", "--no-java", "--no-syntax-extensions"});
    }

    @Test
    public void executeSuccess() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();

        String formula = "huobipro_btc_usdt*usd_in_cny*1.01";

        Map<String, FormulaVariable> contextMap = Maps.newHashMap();

        contextMap.put("huobipro_btc_usdt", new FormulaVariable(
                "huobipro_btc_usdt",
                new BigDecimal("10000"),
                currentTimeMillis,
                1000L * 60              // 60s
        ));

        contextMap.put("usd_in_cny", new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7"),
                currentTimeMillis,
                1000L * 60              // 60s
        ));

        FormulaExecutor executor = new FormulaExecutor(
                formula,
                engine,
                contextMap
        );

        FormulaResult formulaResult = executor.eval();
        Assert.assertNotNull("formulaResult must not null", formulaResult);
        Assert.assertTrue("formulaResult must not null", StringUtils.isNotBlank(formulaResult.getResult()));

        BigDecimal finalResult = new BigDecimal("70700.0");

        Assert.assertTrue("final result must be: " + finalResult, new BigDecimal(formulaResult.getResult()).compareTo(finalResult) == 0);
        Assert.assertTrue("context not empty", formulaResult.getContext() != null && !formulaResult.getContext().isEmpty());

        logger.info("formulaResult:{}", formulaResult);
        logger.info("formulaResult context:{}", formulaResult.getContext());
    }


    @Test
    public void testFailureForContextNotExists() {
        long currentTimeMillis = System.currentTimeMillis();

        String formula = "huobipro_btc_usdt*usd_in_cny*1.01";

        Map<String, FormulaVariable> contextMap = Maps.newHashMap();

//        contextMap.put("huobipro_btc_usdt", new FormulaVariable(
//                "huobipro_btc_usdt",
//                new BigDecimal("10000"),
//                currentTimeMillis,
//                1000L * 60              // 60s
//        ));

        contextMap.put("usd_in_cny", new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7"),
                currentTimeMillis,
                1000L * 60              // 60s
        ));

        FormulaExecutor executor = new FormulaExecutor(
                formula,
                engine,
                contextMap
        );

        try {
            executor.eval();
        } catch (Exception e) {
            logger.error("testFailureForContextNotExists e:", e);
            Assert.assertTrue("message must contains `undefined` and `huobipro_btc_usdt`",
                    e.getMessage().toLowerCase().contains("\"huobipro_btc_usdt\" is not defined"));

        }
    }

    @Test
    public void testFailureForContextNotValidate() {
        long currentTimeMillis = System.currentTimeMillis();

        String formula = "huobipro_btc_usdt*usd_in_cny*1.01";

        Map<String, FormulaVariable> contextMap = Maps.newHashMap();

        contextMap.put("huobipro_btc_usdt", new FormulaVariable(
                "huobipro_btc_usdt",
                null,                    // invalidate here
                currentTimeMillis,
                1000L * 60              // 60s
        ));

        contextMap.put("usd_in_cny", new FormulaVariable(
                "usd_in_cny",
                new BigDecimal("7"),
                currentTimeMillis,
                1000L * 60              // 60s
        ));

        FormulaExecutor executor = new FormulaExecutor(
                formula,
                engine,
                contextMap
        );

        try {
            executor.eval();
        } catch (Exception e) {
            logger.error("testFailureForContextNotExists e:", e);
        }
    }
}