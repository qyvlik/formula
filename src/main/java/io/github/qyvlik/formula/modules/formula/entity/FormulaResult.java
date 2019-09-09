package io.github.qyvlik.formula.modules.formula.entity;

import java.io.Serializable;
import java.util.Map;

public class FormulaResult implements Serializable {
    private Long cost;
    private String formula;
    private String result;
    private Map<String, FormulaVariable> context;

    public FormulaResult() {

    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, FormulaVariable> getContext() {
        return context;
    }

    public void setContext(Map<String, FormulaVariable> context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "(" + formula + "=" + result + ")";
    }
}
