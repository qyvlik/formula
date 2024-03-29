package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.udojava.evalex.Expression;
import io.github.qyvlik.formula.common.base.AppException;
import io.github.qyvlik.formula.modules.formula.model.CalculateContext;
import io.github.qyvlik.formula.modules.formula.model.CalculateResultData;
import io.github.qyvlik.formula.modules.formula.model.CalculateVariable;
import io.github.qyvlik.formula.modules.formula.model.ErrCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Slf4j
public class FormulaCalculator {

    public static final Expression CONSTANT_EXPRESSION = new Expression("1");

    public CalculateResultData calculate(final String originFormula, CalculateContext context) {

        String formula = originFormula.toLowerCase().replaceAll("\\s+", "");

        Expression expression = new Expression(originFormula, new MathContext(34, RoundingMode.DOWN));

        // 将函数转换成大写
        for (final String upperFunctionName : CONSTANT_EXPRESSION.getDeclaredFunctions()) {
            final String lowerFunctionName = upperFunctionName.toLowerCase();
            formula = formula.replaceAll(lowerFunctionName + "\\(", upperFunctionName + "\\(");
        }

        // 填充变量
        List<CalculateVariable> variables = Lists.newArrayList();
        Set<String> variableNameSet = Sets.newLinkedHashSet(expression.getUsedVariables());
        for (String variableName : variableNameSet) {
            CalculateVariable variable = context.getVariableValue(variableName);
            if (variable == null) {
                throw AppException.create(ErrCode.CALCULATE_VARIABLE_NOT_FOUND, "variable " + variableName + " not exist");
            }
            expression.with(variableName, variable.getValue());
            variables.add(variable);
        }

        BigDecimal result = null;

        try {
            result = expression.eval(false);
        } catch (Exception e) {
            throw AppException.create(ErrCode.CALCULATE_EVAL_FAILURE, e);
        }

        CalculateResultData resultData = new CalculateResultData();
        resultData.setOrigin(originFormula);
        resultData.setFormula(formula);
        resultData.setResult(new BigDecimal(result.stripTrailingZeros().toPlainString()));
        resultData.setVariables(variables);

        return resultData;
    }

}
