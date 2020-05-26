package io.github.qyvlik.formula.modules.formula.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import io.github.qyvlik.formula.modules.formula.service.FormulaVariableService;

import java.util.Map;
import java.util.Set;

public class FormulaVariableMapImpl implements FormulaVariableService {
    private Map<String, FormulaVariable> varMap = Maps.newHashMap();

    @Override
    public Map<String, FormulaVariable> getFormulaVariableMap(Set<String> variableNames) {
        Map<String, FormulaVariable> tmpVarMap = Maps.newHashMap();
        for (String varName : variableNames) {
            FormulaVariable formulaVariable = varMap.get(varName);
            if (formulaVariable != null) {
                tmpVarMap.put(formulaVariable.getName(), new FormulaVariable(formulaVariable));
            } else {
                throw new RuntimeException("variable " + varName + " not exist");
            }
        }

        return tmpVarMap;
    }

    @Override
    public FormulaVariable getFormulaVariable(String variableName) {
        FormulaVariable tmp = varMap.get(variableName);
        if (tmp != null) {
            return new FormulaVariable(tmp.getName(), tmp.getValue(), tmp.getTimestamp(), tmp.getTimeout());
        }
        return null;
    }

    @Override
    public void updateFormulaVariable(FormulaVariable formulaVariable) {
        // todo 存入反向汇率
        varMap.put(formulaVariable.getName(), formulaVariable);
    }

    @Override
    public void deleteFormulaVariable(String variableName) {
        varMap.remove(variableName);
    }

    @Override
    public Set<String> getAllVariableNames() {
        return Sets.newHashSet(varMap.keySet());
    }
}
