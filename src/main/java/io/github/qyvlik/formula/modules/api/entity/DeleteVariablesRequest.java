package io.github.qyvlik.formula.modules.api.entity;

import java.util.List;

public class DeleteVariablesRequest {
    private List<String> variableNames;

    public List<String> getVariableNames() {
        return variableNames;
    }

    public void setVariableNames(List<String> variableNames) {
        this.variableNames = variableNames;
    }
}
