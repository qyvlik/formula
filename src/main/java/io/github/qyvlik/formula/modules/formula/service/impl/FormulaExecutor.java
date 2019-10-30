package io.github.qyvlik.formula.modules.formula.service.impl;

import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
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
        formulaResult.setTs(startTime);

        Object result = null;
        try {

            engineBindings(engine, contextMap);

            result = engine.eval(formula);

            formulaResult.setResult(result.toString());

        } catch (Exception e) {
            logger.debug("eval fail : error:{}", e.getMessage());

            throw new RuntimeException(e);
        } finally {
            clearEngineBindings(engine, contextMap);

            logger.debug("eval formula:{} {} time:{} ms",
                    formula, result, System.currentTimeMillis() - startTime);
        }

        return formulaResult;
    }

    private void engineBindings(ScriptEngine engine,
                                Map<String, FormulaVariable> contextMap) {
        ScriptObjectMirror engineBindings = (ScriptObjectMirror) engine.getBindings(SimpleScriptContext.ENGINE_SCOPE);

        for (Map.Entry<String, FormulaVariable> entry : contextMap.entrySet()) {
            FormulaVariable variable = entry.getValue();
            engineBindings.put(entry.getKey(), variable.getValue().doubleValue());
        }
    }

    private void clearEngineBindings(ScriptEngine engine,
                                     Map<String, FormulaVariable> contextMap) {
        try {
            ScriptObjectMirror engineBindings = (ScriptObjectMirror)
                    engine.getBindings(SimpleScriptContext.ENGINE_SCOPE);

            for (Map.Entry<String, FormulaVariable> entry : contextMap.entrySet()) {
                engineBindings.remove(entry.getKey());
            }
        } catch (Exception e) {
            logger.error("clearEngineBindings failure : {}", e.getMessage());
        }

    }
}
