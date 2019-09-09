package io.github.qyvlik.formula.modules.formula.service.impl;

import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.math.BigDecimal;
import java.util.Map;

public class FormulaExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String formula;
    private ScriptEngine engine;
    private Map<String, FormulaVariable> contextMap;

    public FormulaExecutor(String formula,
                           ScriptEngine engine,
                           Map<String, FormulaVariable> contextMap) {
        this.formula = formula;
        this.engine = engine;
        this.contextMap = contextMap;
    }

    public FormulaResult eval() {
        long startTime = System.currentTimeMillis();

        FormulaResult formulaResult = new FormulaResult();
        formulaResult.setFormula(formula);
        formulaResult.setContext(contextMap);
        ScriptContext scriptContext = getScriptContext(contextMap, startTime);

        Object result = null;
        try {
            result = engine.eval(formula, scriptContext);

            formulaResult.setResult(new BigDecimal(result.toString()).toPlainString());

        } catch (ScriptException e) {
            logger.debug("eval fail : error:{}", e.getMessage());

            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("eval formula:{} {} time:{} ms",
                    formula, result, System.currentTimeMillis() - startTime);
        }

        return formulaResult;
    }

    private ScriptContext getScriptContext(Map<String, FormulaVariable> contextMap, Long currentTimeMillis) {
        ScriptContext scriptContext = new SimpleScriptContext();
        for (Map.Entry<String, FormulaVariable> entry : contextMap.entrySet()) {
            FormulaVariable variable = entry.getValue();

            // variable is timeout
            if (variable.getTimestamp() + variable.getTimeout() < currentTimeMillis) {
                logger.debug("getScriptContext failure : name:{} timeout", variable.getName());
                continue;
            }

            scriptContext.setAttribute(entry.getKey(), variable.getValue().doubleValue(),
                    SimpleScriptContext.ENGINE_SCOPE);
        }
        return scriptContext;
    }
}
