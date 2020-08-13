package io.github.qyvlik.formula.modules.formula.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class FormulaVariable implements Serializable {
    private String name;
    private BigDecimal value;
    private Long timestamp;             // create-time, unit is ms
    private Long timeout;               // timeout, unit is ms

    public FormulaVariable(FormulaVariable other) {
        this.name = other.getName();
        this.value = other.getValue();
        this.timestamp = other.getTimestamp();
        this.timeout = other.getTimeout();
    }

    public FormulaVariable(String name, BigDecimal value, Long timestamp, Long timeout) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "(" + name + "=" + value + ")";
    }
}

