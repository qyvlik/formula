package io.github.qyvlik.formula.modules.api.entity;

import io.github.qyvlik.formula.modules.formula.entity.FormulaVariable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateVariablesRequest {
    private List<FormulaVariable> variables;
}
