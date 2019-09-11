package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        engineRemoveAndSetBindings(engine, contextMap);

        Object result = null;
        try {
            result = engine.eval(formula);

            formulaResult.setResult(result.toString());

        } catch (ScriptException e) {
            logger.debug("eval fail : error:{}", e.getMessage());

            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error("eval fail : other error:{}", e.getMessage());
        } finally {
            logger.info("eval formula:{} {} time:{} ms",
                    formula, result, System.currentTimeMillis() - startTime);
        }

        return formulaResult;
    }

    private void engineRemoveAndSetBindings(ScriptEngine engine,
                                            Map<String, FormulaVariable> contextMap) {
        ScriptObjectMirror engineBindings = (ScriptObjectMirror) engine.getBindings(SimpleScriptContext.ENGINE_SCOPE);

        Set<String> variableNames = Sets.newHashSet(engineBindings.getOwnKeys(true));

        List<String> whiteVariableNames = Lists.newArrayList(
                "__FILE__", "__DIR__", "__LINE__",
                "undefined", "NaN", "Infinity", "arguments",
                "Math"
        );

        for (String variable : variableNames) {
            if (whiteVariableNames.contains(variable)) {
                logger.debug("white variable:{}", variable);
                continue;
            }
            engineBindings.remove(variable);
        }

        for (Map.Entry<String, FormulaVariable> entry : contextMap.entrySet()) {
            FormulaVariable variable = entry.getValue();
            engineBindings.put(entry.getKey(), variable.getValue().doubleValue());
        }
    }
}
