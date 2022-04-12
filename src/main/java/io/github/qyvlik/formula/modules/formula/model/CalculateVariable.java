package io.github.qyvlik.formula.modules.formula.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculateVariable {
    private String name;
    private BigDecimal value;
    private MarketPrice market;
}
