package io.github.qyvlik.formula.modules.formula.service;

import com.google.common.collect.Maps;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.impl.FormulaEval;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
public class FormulaEvalTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private NashornScriptEngineFactory factory;
    private ScriptEngine engine;

    @BeforeAll
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


        FormulaResult formulaResult = FormulaEval.eval(formula, engine, contextMap);
        assertNotNull(formulaResult, "formulaResult must not null" );
        assertTrue(StringUtils.isNotBlank(formulaResult.getResult()),"formulaResult must not null");

        BigDecimal finalResult = new BigDecimal("70700.0");

        assertTrue(new BigDecimal(formulaResult.getResult()).compareTo(finalResult) == 0, "final result must be: " + finalResult );
        assertTrue(formulaResult.getContext() != null && !formulaResult.getContext().isEmpty(), "context not empty");

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


        try {
            FormulaEval.eval(formula, engine, contextMap);
        } catch (Exception e) {
            logger.error("testFailureForContextNotExists e:", e);
            assertTrue(e.getMessage().toLowerCase().contains("\"huobipro_btc_usdt\" is not defined"),
                    "message must contains `undefined` and `huobipro_btc_usdt`");
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

        try {
            FormulaEval.eval(formula, engine, contextMap);
        } catch (Exception e) {
            logger.error("testFailureForContextNotExists e:", e);
        }
    }
}