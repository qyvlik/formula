package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.script.ScriptEngine;
import java.util.Map;
import java.util.Set;

public class FormulaCalculatorImpl implements FormulaCalculator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private Map<String, String> aliasMap;
    private FormulaVariableService formulaVariableService;

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(Map<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public FormulaVariableService getFormulaVariableService() {
        return formulaVariableService;
    }

    public void setFormulaVariableService(FormulaVariableService formulaVariableService) {
        this.formulaVariableService = formulaVariableService;
    }

    @Override
    public FormulaResult calculate(String formula) {
        if (!validateFormula(formula)) {
            throw new RuntimeException("formula contains invalid key word");
        }

        StopWatch stopWatch = new StopWatch("calculate");

        stopWatch.start("getScriptEngine");
        ScriptEngine engine = factory.getScriptEngine(new String[]{"-strict", "--no-java", "--no-syntax-extensions"});
        stopWatch.stop();

        stopWatch.start("replaceVariable");
        formula = replaceVariable(formula, aliasMap);
        stopWatch.stop();

        stopWatch.start("getVariableNamesFromFormula");
        Set<String> variableNames = getVariableNamesFromFormula(formula);
        stopWatch.stop();

        stopWatch.start("getFormulaVariableMap");
        Map<String, FormulaVariable> variableMap = formulaVariableService.getFormulaVariableMap(variableNames);
        stopWatch.stop();

        FormulaExecutor executor = new FormulaExecutor(
                formula,
                engine,
                variableMap
        );

        stopWatch.start("eval");
        FormulaResult formulaResult = executor.eval();
        stopWatch.stop();

        if (stopWatch.getTotalTimeMillis() > 100) {
            logger.debug("calculate : formula:{}", formula, stopWatch.prettyPrint());
        }

        formulaResult.setCost(stopWatch.getTotalTimeMillis());

        return formulaResult;
    }

    private Set<String> getVariableNamesFromFormula(String formula) {
        formula = formula.replaceAll("\\s+", "");
        String[] variables = formula.split("\\+|\\-|\\*|\\/|%|\\(|\\)");
        Set<String> names = Sets.newHashSet();
        for (String variableName : variables) {
            if (StringUtils.isBlank(variableName)) {
                continue;
            }
            names.add(variableName);
        }
        return names;
    }

    private String replaceVariable(String script, Map<String, String> variableAliasMap) {
        // 全部转小写
        script = script.toLowerCase();

        // 替换数学公式
        script = handleScript(script);

        if (variableAliasMap == null || variableAliasMap.isEmpty()) {
            return script;
        }

        // 替换别名
        for (Map.Entry<String, String> entry : variableAliasMap.entrySet()) {
            script = script.replaceAll(entry.getKey(), entry.getValue());
        }

        return script;
    }

    private String handleScript(String script) {
        script = script.replaceAll("max\\(", "Math.max\\(");
        script = script.replaceAll("min\\(", "Math.min\\(");
        script = script.replaceAll("round\\(", "Math.round\\(");
        script = script.replaceAll("floor\\(", "Math.floor\\(");
        script = script.replaceAll("ceil\\(", "Math.ceil\\(");
        script = script.replaceAll("abs\\(", "Math.abs\\(");
        return script;
    }

    private boolean validateFormula(String formula) {
        if (StringUtils.isBlank(formula)) {
            return false;
        }
        // white keyword: `+`, `-`, `*`, `/`, `%`, `(`, `)`
        // black keyword
        if (formula.contains("while")
                || formula.contains("if")
                || formula.contains("for")
                || formula.contains("switch")
                || formula.contains("case")
                || formula.contains("try")
                || formula.contains("catch")
                || formula.contains("throw")
                || formula.contains("with")
                || formula.contains("function")
                || formula.contains("new")
                || formula.contains("var")
                || formula.contains("true")
                || formula.contains("false")

                || formula.contains("|")
                || formula.contains("&")
                || formula.contains("^")
                || formula.contains("!")

                || formula.contains("{")
                || formula.contains("}")
                || formula.contains("[")
                || formula.contains("]")
                || formula.contains("\"")
                || formula.contains("\'")
                || formula.contains("=")
                || formula.contains(":")
                || formula.contains(",")
                || formula.contains(";")

                // function
                || formula.contains("call")
                || formula.contains("apply")
                || formula.contains("toString")
                || formula.contains("join")
                || formula.contains("print")

                // properties
                || formula.contains("prototype")

                // type
                || formula.contains("Array")
                || formula.contains("Number")
                || formula.contains("Function")
                || formula.contains("Object")

                // global object
                || formula.contains("console")
                || formula.contains("Math")
                ) {
            return false;
        }
        return true;
    }

}
