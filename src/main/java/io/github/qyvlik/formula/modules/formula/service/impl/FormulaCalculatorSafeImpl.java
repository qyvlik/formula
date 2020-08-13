package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Sets;
import com.udojava.evalex.Expression;
import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.FormulaCalculator;
import io.github.qyvlik.formula.modules.formula.service.FormulaVariableService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class FormulaCalculatorSafeImpl implements FormulaCalculator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> aliasMap;
    private FormulaVariableService formulaVariableService;

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(Map<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    @Override
    public FormulaVariableService getFormulaVariableService() {
        return formulaVariableService;
    }

    public void setFormulaVariableService(FormulaVariableService formulaVariableService) {
        this.formulaVariableService = formulaVariableService;
    }

    @Override
    public FormulaResult calculate(String formula) {
        if (StringUtils.isBlank(formula)) {
            throw new RuntimeException("calculate formula failure : formula is blank");
        }

        StopWatch stopWatch = new StopWatch("calculate");

        stopWatch.start("handleFormula");
        // 全部转小写，并去除空白字符
        formula = formula.toLowerCase().replaceAll("\\s+", "");
        formula = replaceVariable(formula, getAliasMap());
        stopWatch.stop();

        stopWatch.start("getFormulaVariableMap");
        Expression expression = new Expression(formula);
        Set<String> variableSet = Sets.newHashSet(expression.getUsedVariables());

        for (String variable : variableSet) {
            logger.info("calculate validateVariable formula:{}, variable:{}",formula, variable);
            FormulaHelper.validateVariable(formula, variable);
        }

        Map<String, FormulaVariable> formulaVariableMap =
                formulaVariableService.getFormulaVariableMap(variableSet);
        for (Map.Entry<String, FormulaVariable> entry : formulaVariableMap.entrySet()) {
            FormulaVariable value = entry.getValue();
            if (value != null && value.getValue() != null) {
                String variableName = entry.getKey();
                expression.with(variableName, value.getValue());
            }
        }
        stopWatch.stop();


        stopWatch.start("eval");
        BigDecimal result = expression.eval();
        stopWatch.stop();

        FormulaResult formulaResult = new FormulaResult();
        formulaResult.setFormula(formula);
        formulaResult.setContext(formulaVariableMap);
        formulaResult.setTs(System.currentTimeMillis());
        formulaResult.setCost(stopWatch.getTotalTimeMillis());
        formulaResult.setResult(result.stripTrailingZeros().toPlainString());

        return formulaResult;
    }

    @Override
    public FormulaResult convert(String from, String to, BigDecimal fromValue) {
        return FormulaHelper.convert(formulaVariableService, from, to, fromValue);
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
        script = script.replaceAll("max\\(", "MAX\\(");
        script = script.replaceAll("min\\(", "MIN\\(");
        script = script.replaceAll("round\\(", "ROUND\\(");
        script = script.replaceAll("floor\\(", "FLOOR\\(");
        script = script.replaceAll("ceil\\(", "CEIL\\(");
        script = script.replaceAll("abs\\(", "ABS\\(");
        return script;
    }

}
