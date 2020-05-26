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
        long currentTimeMillis = System.currentTimeMillis();

        for (String varName : variableNames) {
            FormulaVariable formulaVariable = varMap.get(varName);

            if (formulaVariable == null) {
                throw new RuntimeException("variable " + varName + " not exist");
            }
            if (formulaVariable.getTimestamp() + formulaVariable.getTimeout() < currentTimeMillis) {
                throw new RuntimeException("variable "
                        + formulaVariable.getName() + " expired");
            }
            tmpVarMap.put(formulaVariable.getName(), new FormulaVariable(formulaVariable));
        }

        return tmpVarMap;
    }

    @Override
    public FormulaVariable getFormulaVariable(String variableName) {
        FormulaVariable tmp = varMap.get(variableName);
        if (tmp != null) {
            return new FormulaVariable(tmp);
        }
        return null;
    }

    @Override
    public void updateFormulaVariable(FormulaVariable formulaVariable) {
        FormulaVariable temp = new FormulaVariable(formulaVariable);
        temp.setName(formulaVariable.getName().toLowerCase());
        varMap.put(temp.getName(), temp);
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
