package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import io.github.qyvlik.formula.modules.formula.service.FormulaVariableService;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormulaCalculatorImpl implements FormulaCalculator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private final ThreadLocal<ScriptEngine> engineThreadLocal = new ThreadLocal<>();

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

    private ScriptEngine createScriptEngine() {
        if (engineThreadLocal.get() == null) {
            ScriptEngine engine = factory.getScriptEngine(
                    new String[]{"-strict", "--no-java", "--no-syntax-extensions"});
            engineThreadLocal.set(engine);
        }
        return engineThreadLocal.get();
    }

    @Override
    public FormulaResult calculate(String formula) {
        if (StringUtils.isBlank(formula)) {
            throw new RuntimeException("calculate formula failure : formula is blank");
        }

        // 全部转小写，并去除空白字符
        formula = formula.toLowerCase().replaceAll("\\s+", "");
        validateFormula(formula);

        StopWatch stopWatch = new StopWatch("calculate");

        stopWatch.start("replaceVariable");
        formula = replaceVariable(formula, getAliasMap());
        stopWatch.stop();

        Set<String> variableNames = getVariableNamesFromFormula(formula);

        stopWatch.start("getFormulaVariableMap");
        Map<String, FormulaVariable> variableMap =
                formulaVariableService.getFormulaVariableMap(variableNames);
        stopWatch.stop();

        stopWatch.start("getScriptEngine");
        ScriptEngine engine = createScriptEngine();
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
            logger.debug("calculate : formula:{} {}", formula, stopWatch.prettyPrint());
        }

        formulaResult.setCost(stopWatch.getTotalTimeMillis());

        return formulaResult;
    }

    private Set<String> getVariableNamesFromFormula(String formula) {
        String[] variables = formula.split("\\+|\\-|\\*|\\/|%|\\(|\\)|,");
        Set<String> names = Sets.newHashSet();
        for (String variableName : variables) {
            if (StringUtils.isBlank(variableName)) {
                continue;
            }
            if (variableName.startsWith("Math.")) {
                continue;
            }
            if (isNumeric(variableName)) {
                continue;
            }
            names.add(variableName);
        }
        return names;
    }

    private boolean isNumeric(String strNum) {
        try {
            new BigDecimal(strNum);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String replaceVariable(String formulaScript, Map<String, String> variableAliasMap) {
        // 替换数学公式
        formulaScript = handleScript(formulaScript);

        if (variableAliasMap == null || variableAliasMap.isEmpty()) {
            return formulaScript;
        }

        // 替换别名
        // from: okex_,  such as `okex_xxx_xxx`
        // to:   okex3_, such as `okex3_xxx_xxx`
        for (Map.Entry<String, String> entry : variableAliasMap.entrySet()) {
            formulaScript = formulaScript.replaceAll(entry.getKey(), entry.getValue());
        }

        return formulaScript;
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

    private void validateFormula(String formula) {
        List<String> blackKeywords = Lists.newArrayList(
                "while", "if", "for", "switch", "case",
                "try", "catch", "throw", "with",
                "function", "new", "delete",
                "var", "true", "false",

                "|", "&", "^", "!", "{", "}", "[", "]", "\"", "'", "\\", "=", ":", ";",

                "this",

                "length", "name", "apply", "bind", "call", "caller", "constructor", "hasOwnProperty",
                "isPrototypeOf", "propertyIsEnumerable", "valueOf",
                "__defineSetter__", "__defineSetter__", "__lookupGetter__", "__lookupSetter__", "__proto__",
                "toLocaleString", "toString",

                "join",

                "prototype", "global",
                "Array", "Number", "String", "Function", "Object",
                "Java", "java", "arguments", "console", "eval", "Math",

                "print", "load", "loadWithNewGlobal", "javax.script", "javax",
                "script", "exit", "quit"
        );

        for (String keyword : blackKeywords) {
            if (formula.contains(keyword.toLowerCase())) {
                throw new RuntimeException("validateFormula failure : formula contains black keyword: `" + keyword + "`");
            }
        }
    }
}
