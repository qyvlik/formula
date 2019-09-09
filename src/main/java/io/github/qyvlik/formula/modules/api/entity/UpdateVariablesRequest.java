package io.github.qyvlik.formula.modules.api.entity;

import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;

import java.util.List;

public class UpdateVariablesRequest {
    private List<FormulaVariable> variables;

    public List<FormulaVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<FormulaVariable> variables) {
        this.variables = variables;
    }
}
