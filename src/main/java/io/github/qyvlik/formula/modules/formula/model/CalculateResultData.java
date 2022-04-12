package io.github.qyvlik.formula.modules.formula.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CalculateResultData {
    private String origin;
    private String formula;
    private BigDecimal result;
    private List<CalculateVariable> variables;
}
