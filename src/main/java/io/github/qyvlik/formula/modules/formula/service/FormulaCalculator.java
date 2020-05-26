package io.github.qyvlik.formula.modules.formula.service;

import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;

import java.math.BigDecimal;

public interface FormulaCalculator {
    FormulaResult calculate(String formula);

    FormulaResult convert(String from, String to, BigDecimal fromValue);
}
