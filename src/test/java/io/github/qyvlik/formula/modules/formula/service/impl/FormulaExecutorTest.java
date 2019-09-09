package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class FormulaExecutorTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Test
    public void execute() throws Exception {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new String[]{"-strict", "--no-java", "--no-syntax-extensions"});

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

        logger.info("formulaResult:{}", formulaResult);
        logger.info("formulaResult context:{}", formulaResult.getContext());
    }

    @Test
    public void testParseVariableFromFormula() {
        String formula = "huobipro_btc_usdt*(usd_in_cny*1.01) + (1 + 1)";
        formula = formula.replaceAll("\\s+", "");
        String[] variables = formula.split("\\+|\\-|\\*|\\/|%|\\(|\\)");
        Set<String> variableSet = Sets.newHashSet(variables);
        logger.info("variables:{}", variableSet);
    }

}