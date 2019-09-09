package io.github.qyvlik.formula.modules.formula.service;

import io.github.qyvlik.formula.modules.formula.entity.FormulaResult;

public interface FormulaCalculator {
    FormulaResult calculate(String formula);
}
