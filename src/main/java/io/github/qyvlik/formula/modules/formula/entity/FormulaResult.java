package io.github.qyvlik.formula.modules.formula.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class FormulaResult implements Serializable {
    private Long cost;
    private Long ts;
    private String formula;
    private String result;
    private Map<String, FormulaVariable> context;

    @Override
    public String toString() {
        return "(" + formula + "=" + result + ")";
    }
}
