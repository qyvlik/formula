package io.github.qyvlik.formula.modules.formula.service;

import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;

import java.util.Map;
import java.util.Set;

public interface FormulaVariableService {

    Map<String, FormulaVariable> getFormulaVariableMap(Set<String> variableNames);

    FormulaVariable getFormulaVariable(String variableName);

    void updateFormulaVariable(FormulaVariable formulaVariable);

    void deleteFormulaVariable(String variableName);

    Set<String> getAllVariableNames();
}
